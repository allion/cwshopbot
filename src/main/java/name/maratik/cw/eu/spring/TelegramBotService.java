//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.spring;

import com.google.common.collect.ImmutableMap;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import name.maratik.cw.eu.spring.model.TelegramBotCommand;
import name.maratik.cw.eu.spring.model.TelegramHandler;
import name.maratik.cw.eu.spring.model.TelegramMessageCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class TelegramBotService {
    private static final Logger logger = LogManager.getLogger(TelegramBotService.class);

    private final String username;
    private final String token;
    private final String path;


    private final TelegramBotsApi api;

    private final Map<String, TelegramHandler> commandList = new LinkedHashMap<>();
    private final Executor botExecutor;
    private TelegramHandler defaultMessageHandler;

    private final DefaultAbsSender client;
    private final Map<Type, BiFunction<TelegramMessageCommand, Update, ?>> argumentMapper;

    public TelegramBotService(TelegramBotBuilder botBuilder) {
        logger.info("Build TelegramBot: {}", botBuilder);

        username = botBuilder.getUsername();
        token = botBuilder.getToken();
        path = botBuilder.getPath();

        api = new TelegramBotsApi();

        try {
            switch (botBuilder.getType()) {
                case LONG_POLLING:
                    botExecutor = Executors.newFixedThreadPool(
                        botBuilder.getMaxThreads() > 0
                            ? botBuilder.getMaxThreads()
                            : TelegramBotBuilder.DEFAULT_MAX_THREADS
                    );
                    TelegramLongPollingBot longPollingClient = new TelegramBotLongPollingImpl();
                    api.registerBot(longPollingClient);
                    client = longPollingClient;
                    break;
                case WEBHOOK:
                    TelegramWebhookBot webhookClient = new TelegramBotWebhookImpl();
                    api.registerBot(webhookClient);
                    client = webhookClient;
                    botExecutor = null;
                    break;
                default:
                    throw new RuntimeException("Unknown bot type requested: " + botBuilder.getType());
            }
        } catch (TelegramApiException e) {
            logger.error("Error while creating TelegramBotsApi", e);
            throw new RuntimeException(e);
        }

        argumentMapper = ImmutableMap.<Type, BiFunction<TelegramMessageCommand, Update, ?>>builder()
            .put(Update.class, (telegramMessageCommand, update) -> update)
            .put(TelegramMessageCommand.class, (telegramMessageCommand, update) -> telegramMessageCommand)
            .put(String.class, (telegramMessageCommand, update) -> telegramMessageCommand.getArgument().orElse(null))
            .put(TelegramBotsApi.class, (telegramMessageCommand, update) -> api)
            .put(TelegramBotService.class, (telegramMessageCommand, update) -> this)
            .put(DefaultAbsSender.class, (telegramMessageCommand, update) -> client)
            .put(Message.class, (telegramMessageCommand, update) -> update.getMessage())
            .put(User.class, (telegramMessageCommand, update) -> update.getMessage().getFrom())
            .build();
    }

    @SuppressWarnings("WeakerAccess")
    public void updateLongPolling(Update update) {
        logger.debug("updateLongPolling: {}", update);
        CompletableFuture.runAsync(() ->
            updateProcess(update).ifPresent(result -> {
                try {
                    client.execute(result);
                } catch (TelegramApiException e) {
                    logger.error("Update: {}. Can not send message {} to telegram: ", update, result, e);
                }
        }), botExecutor);
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<BotApiMethod<?>> updateProcess(Update update) {
        logger.debug("Update {} received", update);
        if (update.getMessage() == null) {
            return Optional.empty();
        }
        TelegramMessageCommand command = new TelegramMessageCommand(update.getMessage().getText());
        return (command.isCommand()
            ? command.getCommand().map(commandList::get)
            : Optional.ofNullable(defaultMessageHandler)
        ).flatMap(commandHandler -> {
            try {
                Method method = commandHandler.getMethod();
                Object[] arguments = makeArgumentList(method, command, update);

                if (commandHandler.getTelegramCommand().filter(TelegramCommand::isHelp).isPresent()) {
                    sendHelpList(update);
                } else {
                    Type methodGenericReturnType = method.getGenericReturnType();
                    if (methodGenericReturnType == void.class) {
                        method.invoke(commandHandler.getBean(), arguments);
                    } else if (methodGenericReturnType instanceof Class<?> &&
                        BotApiMethod.class.isAssignableFrom((Class<?>) methodGenericReturnType)) {
                        return Optional.ofNullable((BotApiMethod<?>) method.invoke(commandHandler.getBean(), arguments));
                    }
                }
            } catch (Exception e) {
                logger.error("Could not process update: {}", update, e);
            }
            return Optional.empty();
        });
    }

    private void sendHelpList(Update update) throws TelegramApiException {
        client.execute(new SendMessage()
            .setChatId(update.getMessage().getChatId())
            .setText(buildHelpMessage())
        );
    }

    private String buildHelpMessage() {
        StringBuilder sb = new StringBuilder();
        getCommandList().forEach(method -> sb
            .append(method.getCommand())
            .append(' ')
            .append(method.getDescription())
            .append('\n')
        );
        return sb.toString();
    }

    @SuppressWarnings("WeakerAccess")
    public Stream<TelegramBotCommand> getCommandList() {
        return commandList.entrySet().stream()
            .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
            .map(entry -> new TelegramBotCommand(
                entry.getKey(),
                entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
            ));
    }

    public DefaultAbsSender getClient() {
        return client;
    }

    private Object[] makeArgumentList(Method method, TelegramMessageCommand telegramMessageCommand, Update update) {
        return Arrays.stream(method.getGenericParameterTypes())
            .map(type -> argumentMapper.getOrDefault(type, (t, u) -> null))
            .map(mapper -> mapper.apply(telegramMessageCommand, update))
            .toArray();
    }

    public void addHandler(Object bean, Method method) {
        TelegramCommand command = AnnotationUtils.findAnnotation(method, TelegramCommand.class);
        if (command != null) {
            for (String cmd : command.value()) {
                //noinspection ObjectAllocationInLoop
                commandList.put(cmd, new TelegramHandler(bean, method, command));
            }
        }
    }

    public void addDefaultMessageHandler(Object bean, Method method) {
        defaultMessageHandler = new TelegramHandler(bean, method, null);
    }

    public void addHelpMethod() {
        try {
            Method helpMethod = getClass().getMethod("helpMethod");
            TelegramCommand command = AnnotationUtils.findAnnotation(helpMethod, TelegramCommand.class);
            if (command != null) {
                for (String cmd : command.value()) {
                    //noinspection ObjectAllocationInLoop
                    commandList.put(cmd, new TelegramHandler(this, helpMethod, command));
                }
            }
        } catch (Exception e) {
            logger.error("Could not add help method", e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    @TelegramCommand(value = "/help", isHelp = true)
    public void helpMethod() {
    }

    private class TelegramBotLongPollingImpl extends TelegramLongPollingBot {

        @Override
        public void onUpdateReceived(Update update) {
            updateLongPolling(update);
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }
    }

    private class TelegramBotWebhookImpl extends TelegramWebhookBot {

        @Override
        public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
            return updateProcess(update).orElse(null);
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }

        @Override
        public String getBotPath() {
            return path;
        }
    }
}