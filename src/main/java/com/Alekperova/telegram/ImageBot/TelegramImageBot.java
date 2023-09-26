package com.Alekperova.telegram.ImageBot;

import lombok.extern.slf4j.Slf4j;
import com.Alekperova.telegram.ImageBot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.Alekperova.telegram.ImageBot.repository.UserRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramImageBot extends TelegramLongPollingBot {


    @Autowired
    private UserRepository userRepository;

    final BotConfig botConfig;

    final static String HELP_TEXT = "This bot is made to demonstrate my programming skills and does nothing significatnt";

    @Override
    public String getBotToken(){
        return botConfig.token;
    }


    public TelegramImageBot(BotConfig config) {

        this.botConfig = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata", "get information about user"));
        listOfCommands.add(new BotCommand("/cleardata", "clear all data"));
        listOfCommands.add(new BotCommand("/help", "get an info how to use bot"));
        listOfCommands.add(new BotCommand("/settings", "setting bot"));
        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error while setting bot's command list:" + e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            switch (messageText){
                case "/start":
                    registerUser(update.getMessage());
                    break;
                case "/help":
                    sendAnswerMessage(chatId, HELP_TEXT);
                    break;
                case "/mydata":
                    getUserData(chatId);
                    break;
                case "/cleardata":
                    clearUserData(chatId);
                    break;
                    default:
                    sendAnswerMessage(chatId, "Hello i dont understand");
            }
        }
    }


    private void getUserData(Long chatId){
        String data;
        if(userRepository.findByChatId(chatId).isEmpty()){
            data = "no any data received";
        }
        else{
            User user = userRepository.findByChatId(chatId).get();
            data = user.toString();
        }
        sendAnswerMessage(chatId, data);
    }

    private void clearUserData(Long chatId){
        try{
            userRepository.deleteByChatId(chatId);
            sendAnswerMessage(chatId, "your data was deleted");
        } catch (Exception e){
            log.error("error while deleting: " + e);
            sendAnswerMessage(chatId, "something went wrong");
        }
    }

    private void registerUser(Message message){
        if(userRepository.findByChatId(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();
            User user = new User();
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setChatId(chatId);
            user.setUserName(chat.getUserName());
            user.setRegisterTime(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
        }
        String answer = "Hi, " + message.getChat().getUserName();
        sendAnswerMessage(message.getChatId(), answer);
    }

    public  void sendAnswerMessage(Long chatId, String textToSend){
        SendMessage message = new SendMessage(String.valueOf(chatId), textToSend);
        try{
            execute(message);
            System.out.println("a");
        } catch (TelegramApiException e){
            log.error(String.valueOf(e));
        }
    }
}

