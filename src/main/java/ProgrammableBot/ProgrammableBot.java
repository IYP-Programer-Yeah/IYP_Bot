package ProgrammableBot;

import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static String.StringManipulation.*;

/**
 * Created by HoseinGhahremanzadeh on 6/27/2016.
 *
 *
 * create a new instance to run the bot
 */
public class ProgrammableBot {
    private ArrayList<String> whiteListChannels = new ArrayList<String>();
    private ArrayList<String> whiteListRoles = new ArrayList<String>();
    private ArrayList<String> whiteListMembers = new ArrayList<String>();

    private ArrayList<String> blackListChannels = new ArrayList<String>();
    private ArrayList<String> blackListRoles = new ArrayList<String>();
    private ArrayList<String> blackListMembers = new ArrayList<String>();

    private ArrayList<MessageCat> messageCats = new ArrayList<MessageCat>();
    private ArrayList<AvailableResource> availableResources = new ArrayList<AvailableResource>();

    private String mentionTag;

    public void addMemberToWhiteList(String member){
        whiteListMembers.add(member);
    }
    public void addMemberToBlackList(String member){
        blackListMembers.add(member);
    }

    public void addChannelToWhiteList(String channel){
        whiteListChannels.add(channel);
    }
    public void addChannelToBlackList(String channel){
        blackListChannels.add(channel);
    }

    public void addRoleToWhiteList(String role){
        whiteListRoles.add(role);
    }
    public void addRoleToBlackList(String role){
        blackListRoles.add(role);
    }



    public void removeMemberFromWhiteList(String member){
        whiteListMembers.remove(member);
    }
    public void removeMemberFromBlackList(String member){
        blackListMembers.remove(member);
    }

    public void removeChannelFromWhiteList(String channel){
        whiteListChannels.remove(channel);
    }
    public void removeChannelFromBlackList(String channel){
        blackListChannels.remove(channel);
    }

    public void removeRoleFromWhiteList(String role){
        whiteListRoles.remove(role);
    }
    public void removeRoleFromBlackList(String role){
        blackListRoles.remove(role);
    }



    private boolean isMemberWhite (String member){
        return whiteListMembers.contains(member);
    }
    private boolean isMemberBlack (String member){
        return blackListMembers.contains(member);
    }

    private boolean isChannelWhite (String channel){
        return whiteListChannels.contains(channel);
    }
    private boolean isChannelBlack (String channel){
        return blackListChannels.contains(channel);
    }

    private boolean isRoleWhite (String role){
        return whiteListRoles.contains(role);
    }
    private boolean isRoleBlack (String role){
        return blackListRoles.contains(role);
    }


    public void savePermissions(){
        File permissions=new File("permissions");

        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(permissions));

            objectOutputStream.writeObject(blackListChannels);
            objectOutputStream.writeObject(blackListMembers);
            objectOutputStream.writeObject(blackListRoles);
            objectOutputStream.writeObject(whiteListChannels);
            objectOutputStream.writeObject(whiteListMembers);
            objectOutputStream.writeObject(whiteListRoles);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadPermissions(){
        File permissions=new File("permissions");
        if (permissions.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(permissions));

                blackListChannels=(ArrayList<String>) objectInputStream.readObject();
                blackListMembers=(ArrayList<String>) objectInputStream.readObject();
                blackListRoles=(ArrayList<String>) objectInputStream.readObject();
                whiteListChannels=(ArrayList<String>) objectInputStream.readObject();
                whiteListMembers=(ArrayList<String>) objectInputStream.readObject();
                whiteListRoles=(ArrayList<String>) objectInputStream.readObject();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private AvailableResource getResourceByTag (String tag) {
        for (AvailableResource availableResource:availableResources)
            if (availableResource.resourceTag.equals(tag))
                return availableResource;
        return null;
    }

    private MessageCat getMessageCatByTag (String tag) {
        for (MessageCat messageCat:messageCats)
            if (messageCat.messageTag.equals(tag))
                return messageCat;
        return null;
    }

    private String handleAddNewRes(String message) {
        String resourceTag=replaceFirst(message,".new_res ", "");
        if (resourceTag.contains(" "))
            return "The resource name can't contain ' '.";

        if (getResourceByTag(resourceTag)!=null)
            return "Resource already exists.";
        AvailableResource availableResource = new AvailableResource();
        availableResource.resourceTag=resourceTag;
        availableResources.add(availableResource);
        return "Resource is added.";
    }
    private String handleAddToRes(String message) {
        String resourceData=replaceFirst(message,".add_to_res ", "");
        String resourceTag=split(resourceData," ")[0];
        String resource=replaceFirst(resourceData,resourceTag+" ", "");
        AvailableResource availableResource=getResourceByTag(resourceTag);
        if (availableResource!=null) {
            availableResource.resources.add(resource);
            return "Resource added.";
        }
        return "Resource not found.";
    }
    private String handleRemoveFromRes(String message) {
        String resourceData=replaceFirst(message,".remove_from_res ", "");
        String resourceTag=split(resourceData," ")[0];
        String resourceToRemove=replaceFirst(resourceData,resourceTag+" ", "");
        AvailableResource availableResource=getResourceByTag(resourceTag);
        if (availableResource!=null)
            for (String resource : availableResource.resources)
                if (resource.equals(resourceToRemove)) {
                    availableResource.resources.remove(resource);
                    return "Resource removed.";
                }
        return "Resource not found.";
    }
    private String handleAddMessage(String message) {
        String messageData=replaceFirst(message,".add_message ", "");
        String messageCatToFind=split(messageData," ")[0];

        String messageContent=replaceFirst(messageData,messageCatToFind+" ","");

        MessageCat messageCat=getMessageCatByTag(messageCatToFind);
        if (messageCat!=null) {
            messageCat.messages.add(getResTagsFromString(messageContent));
            return "Message added";
        }
        messageCat=new MessageCat();
        messageCat.messageTag=messageCatToFind;
        messageCat.messages.add(getResTagsFromString(messageContent));
        messageCats.add(messageCat);
        return "Message added";
    }
    private String handleAddRespond(String message) {
        String messageData=replaceFirst(message,".add_respond ", "");
        String messageCatToFind=split(messageData," ")[0];

        String messageContent=replaceFirst(messageData,messageCatToFind+" ","");

        MessageCat messageCat=getMessageCatByTag(messageCatToFind);
        if (messageCat!=null) {
            messageCat.responds.add(getResTagsFromString(messageContent));
            return "Respond added";
        }
        messageCat=new MessageCat();
        messageCat.messageTag=messageCatToFind;
        messageCat.responds.add(getResTagsFromString(messageContent));
        messageCats.add(messageCat);
        return "Respond added";
    }

    private ArrayList<String>[] getResTagsFromString (String message) {
        ArrayList<String> resTags=new ArrayList<String>();
        ArrayList<String> resTagIDs=new ArrayList<String>();
        String[] messageParts=split(message,"<");
        for (int i=0;i<messageParts.length;i++)
            if (messageParts[i].contains(">")) {
                String resTag="<"+split(messageParts[i],">")[0]+">";
                resTags.add(resTag);
                messageParts[i]=replaceFirst(("<"+messageParts[i]),resTag,"");
                if (messageParts[i].startsWith("{")) {
                    String res_num=replaceFirst(split(messageParts[i],"}")[0],"{","");
                    resTagIDs.add(res_num);
                    messageParts[i]=replaceFirst(messageParts[i],"{"+res_num+"}","");
                } else {
                    resTagIDs.add(null);
                }
            }
        ArrayList<String>[] msgData=new ArrayList[3];
        msgData[0]=new ArrayList<String>();
        for (String messagePart:messageParts)
            msgData[0].add(messagePart);
        msgData[1]=resTags;
        msgData[2]=resTagIDs;
        return msgData;
    }

    private boolean containsRes(String tag, String resource) {
        AvailableResource availableResource=getResourceByTag(tag);
        if (availableResource==null)
            return false;
        for (String res:availableResource.resources)
            if (res.equals(resource))
                return true;
        return false;
    }

    private String readMessageGenerateRespond (String message, String senderMentionTag, String sendChannel) {
        String respond = null;
        message=message.replaceAll(mentionTag,"@me");
        NextCat:for (MessageCat messageCat : messageCats)
            NextMessage:for (ArrayList<String>[] messageConstant : messageCat.messages) {
                ArrayList<String> res = new ArrayList<String>();
                ArrayList<String> resTag = new ArrayList<String>();
                ArrayList<String> resID = new ArrayList<String>();
                String restOfMessage = message;
                if (messageConstant[0].get(0).length()!=0)
                    if (restOfMessage.startsWith(messageConstant[0].get(0))) {
                        restOfMessage = replaceFirst(restOfMessage,messageConstant[0].get(0), "");
                    } else {
                        continue;
                    }
                for (int i = 1; i < messageConstant[0].size(); i++)
                    if (restOfMessage.contains(messageConstant[0].get(i))) {
                        String[] messageParts;
                        if (messageConstant[0].get(i).length() != 0)
                            messageParts = split(restOfMessage,messageConstant[0].get(i));
                        else {
                            messageParts=new String[1];
                            messageParts[0]=restOfMessage;
                        }
                        String resFound=messageParts[0];
                        if (!containsRes(messageConstant[1].get(res.size()),resFound)) {
                            if (messageParts.length==1)
                                continue NextMessage;
                            for (int k = 1; k < messageParts.length; k++) {
                                resFound += messageConstant[0].get(i);
                                resFound += messageParts[k];
                                if (containsRes(messageConstant[1].get(res.size()), resFound))
                                    break;
                                if (k == (messageParts.length - 1))
                                    continue NextMessage;
                            }
                        }
                        resID.add(messageConstant[2].get(res.size()));
                        resTag.add(messageConstant[1].get(res.size()));
                        res.add(resFound);
                        restOfMessage = replaceFirst(restOfMessage,resFound + messageConstant[0].get(i), "");
                    } else {
                        continue NextMessage;
                    }

                if (messageCat.responds.size()==0) {
                    respond = messageCat.messageTag;
                    return respond;
                }
                respond="";
                Random rnd = new Random();
                ArrayList<String>[] respondStructure = messageCat.responds.get(rnd.nextInt()%messageCat.responds.size());

                NextStructure:for (int i=0;i<respondStructure[0].size();i++){
                    respond=respond+respondStructure[0].get(i);
                    //add resource
                    if (i<respondStructure[2].size()) {
                        AvailableResource currentResource = getResourceByTag(respondStructure[1].get(i));
                        if (currentResource == null || currentResource.resources.size() == 0) {
                            respond = respond + "(Invalid Resource)";
                            continue;
                        }

                        if (respondStructure[2].get(i) == null)
                            respond = respond + currentResource.resources.get(rnd.nextInt() % currentResource.resources.size());
                        else {
                            for (int j=0;j<resID.size();j++)
                                if(resID.get(j).equals(respondStructure[2].get(i))&&resTag.get(j).equals(respondStructure[1].get(i))){
                                    respond = respond + res.get(j);
                                    continue NextStructure;
                                }
                            respond = respond + currentResource.resources.get(rnd.nextInt() % currentResource.resources.size());
                        }
                    }
                }
                respond = respond.replace("@sender", senderMentionTag);
                respond = respond.replace("#sent-channel", sendChannel);
                respond = respond.replace("@me", mentionTag);
                return respond;
            }

        return respond;
    }


    private void resetDatabase(){
        File db = new File("F:\\IYPBot Database\\" + "sgafgdffsffgsdfg.db");
            if (db.exists()) {
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(db));
                    availableResources = (ArrayList<AvailableResource>) objectInputStream.readObject();
                    messageCats = (ArrayList<MessageCat>) objectInputStream.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public void handleMessages(Message message, boolean isWhite){
        String messageContent = message.getContent();
        while (messageContent.endsWith(" "))
            messageContent = replaceLast(messageContent, " ", "");
        messageContent = messageContent.toLowerCase();
        if (messageContent.startsWith(".new_res ") && isWhite)
            message.reply(handleAddNewRes(messageContent));
        else if ((messageContent.startsWith(".add_to_res ")) && isWhite)
            message.reply(handleAddToRes(messageContent));
        else if ((messageContent.startsWith(".remove_from_res ")) && isWhite)
            message.reply(handleRemoveFromRes(messageContent));
        else if ((messageContent.startsWith(".add_message ")) && isWhite)
            message.reply(handleAddMessage(messageContent));
        else if ((messageContent.startsWith(".add_respond ")) && isWhite)
            message.reply(handleAddRespond(messageContent));
        else if (messageContent.equals("?help") && isWhite) {
            message.getChannelReceiver().sendMessage("add a new resource ```.new_res <resource-tag>```\nadd something to resource ```.add_to_res <resource-tag> res```\nremove something from resource ```.remove_from_res <resource-tag> res```");
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            message.getChannelReceiver().sendMessage("\n\nadd a message to category ```.add_message [category] message content (can include any of the tags below)```\nadd a respond message to category ```.add_respond [category] message content (can include any of the tags below)```\n\n*Tags:*\n\nplaceholder for resource");
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            message.getChannelReceiver().sendMessage("```<resource-tag>{resource-num}```\nusing\n```{resource-num}```is optional, use it if you want to use the same resource in the response\n\nplaceholder for the sender of message```@sender```\nplaceholder for name of the channel```#sent-channel```\nplaceholder for bot```@me```");
        } else if (messageContent.startsWith(".save db ") && isWhite) {
            File db = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".save db ", ""));
            if (!db.getAbsolutePath().contains("..")) {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(db));
                    objectOutputStream.writeObject(availableResources);
                    objectOutputStream.writeObject(messageCats);
                    objectOutputStream.flush();
                    message.reply("database saved");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else
                message.reply("access denied");
        } else if (messageContent.startsWith(".load db ") && isWhite) {
            File db = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".load db ", ""));
            if (!db.getAbsolutePath().contains("..")) {
                if (db.exists()) {
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(db));
                        availableResources = (ArrayList<AvailableResource>) objectInputStream.readObject();
                        messageCats = (ArrayList<MessageCat>) objectInputStream.readObject();
                        message.reply("database loaded");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    message.reply("database not found");
            } else
                message.reply("access denied");
        }else if (messageContent.equals(".reset")&&isWhite) {
            resetDatabase();
            message.reply("database loaded");
            
        }else if (messageContent.startsWith(".add white member ")&&isWhite) {
            addMemberToWhiteList(replaceFirst(messageContent,".add white member ",""));
            message.reply("member added to white list.");
            savePermissions();

        }else if (messageContent.startsWith(".add black member ")&&isWhite) {
            addMemberToBlackList(replaceFirst(messageContent,".add black member ",""));
            message.reply("member added to black list.");
            savePermissions();



        }else if (messageContent.startsWith(".add white channel ")&&isWhite) {
            addChannelToWhiteList(replaceFirst(messageContent,".add white channel ",""));
            message.reply("channel added to white list.");
            savePermissions();

        }else if (messageContent.startsWith(".add black channel ")&&isWhite) {
            addChannelToBlackList(replaceFirst(messageContent,".add black channel ",""));
            message.reply("channel added to black list.");
            savePermissions();



        }else if (messageContent.startsWith(".add white role ")&&isWhite) {
            addRoleToWhiteList(replaceFirst(messageContent,".add white role ",""));
            message.reply("role added to white list.");
            savePermissions();

        }else if (messageContent.startsWith(".add black role ")&&isWhite) {
            addRoleToBlackList(replaceFirst(messageContent,".add black role ",""));
            message.reply("role added to black list.");
            savePermissions();


            
            
            
            
        }else if (messageContent.startsWith(".remove white member ")&&isWhite) {
            removeMemberFromWhiteList(replaceFirst(messageContent,".remove white member ",""));
            message.reply("member removed from white list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove black member ")&&isWhite) {
            removeMemberFromBlackList(replaceFirst(messageContent,".remove black member ",""));
            message.reply("member removed from black list.");
            savePermissions();



        }else if (messageContent.startsWith(".remove white channel ")&&isWhite) {
            removeChannelFromWhiteList(replaceFirst(messageContent,".remove white channel ",""));
            message.reply("channel removed from white list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove black channel ")&&isWhite) {
            removeChannelFromBlackList(replaceFirst(messageContent,".remove black channel ",""));
            message.reply("channel removed from black list.");
            savePermissions();



        }else if (messageContent.startsWith(".remove white role ")&&isWhite) {
            removeRoleFromWhiteList(replaceFirst(messageContent,".remove white role ",""));
            message.reply("role removed from white list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove black role ")&&isWhite) {
            removeRoleFromBlackList(replaceFirst(messageContent,".remove black role ",""));
            message.reply("role removed from black list.");
            savePermissions();


        }else {
            String respond = readMessageGenerateRespond(messageContent, message.getAuthor().getMentionTag(), message.getChannelReceiver().getMentionTag());
            if (respond != null)
                message.reply(respond);
        }

    }

    public ProgrammableBot(String token) {
        DiscordAPI api = Javacord.getApi(token, true);
        // connect
        api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(DiscordAPI api) {


                mentionTag=api.getYourself().getMentionTag();

                api.setGame("?help for help");

                resetDatabase();
                loadPermissions();
                // register listener
                api.registerListener(new MessageCreateListener() {
                    public void onMessageCreate(DiscordAPI api, Message message) {
                        String memberName=message.getAuthor().getMentionTag();
                        String channelName=message.getChannelReceiver().getMentionTag();

                        Collection<Role> rolesCollection = message.getAuthor().getRoles(message.getChannelReceiver().getServer());
                        Role[] roles = new Role[rolesCollection.size()];
                        roles = rolesCollection.toArray(roles);

                        String roleName=roles.length == 0 ? null : roles[0].getName().toLowerCase();

                        boolean isBlack=isMemberBlack(memberName)||isChannelBlack(channelName)||isRoleBlack(roleName);

                        if (!isBlack) {
                            boolean isWhite=(isMemberWhite(memberName)||isRoleWhite(roleName))&&isChannelWhite(channelName);
                            handleMessages(message,isWhite);
                        }

                    }
                });
            }
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
