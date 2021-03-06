package ProgrammableBot;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import static String.StringManipulation.*;

/**
 * Created by HoseinGhahremanzadeh on 6/27/2016.
 *
 *
 * create a new instance to run the bot
 */
public class ProgrammableBot {

    private ArrayList<String> classNames = new ArrayList<String>();
    private ArrayList<Class> classes = new ArrayList<Class>();

    public String runProgram(String className, String functionName, String[] args) {
        try {

            URLClassLoader classLoader = null;
            try {
                classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Object[] arg={args};
            Class funcClass;
            int index=classNames.indexOf(className);
            if (index!=-1)
                funcClass=classes.get(index);
            else {
                funcClass=Class.forName(className, true, classLoader);
                classes.add(funcClass);
                classNames.add(className);
            }
            Object ret = funcClass.getDeclaredMethod(functionName, new Class[]{String[].class}).invoke(null, arg);
            return (String)ret;
        } catch (ClassNotFoundException e) {
            return "(Invalid Class)";
        } catch (Exception e) {
            return "(Invalid Method)";
        }
    }






    private ArrayList<String> whiteListChannels = new ArrayList<String>();
    private ArrayList<String> whiteListRoles = new ArrayList<String>();
    private ArrayList<String> whiteListMembers = new ArrayList<String>();

    private ArrayList<String> blackListChannels = new ArrayList<String>();
    private ArrayList<String> blackListRoles = new ArrayList<String>();
    private ArrayList<String> blackListMembers = new ArrayList<String>();

    private ArrayList<String> adminListRoles = new ArrayList<String>();
    private ArrayList<String> adminListMembers = new ArrayList<String>();


    private ArrayList<MessageCat> messageCats = new ArrayList<MessageCat>();
    private ArrayList<AvailableResource> availableResources = new ArrayList<AvailableResource>();
    private ArrayList<Program> programs = new ArrayList<Program>();

    private String mentionTag;

    public void addMemberToWhiteList(String member){
        whiteListMembers.add(member);
    }
    public void addMemberToBlackList(String member){
        blackListMembers.add(member);
    }
    public void addMemberToAdminList(String member){
        adminListMembers.add(member);
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
    public void addRoleToAdminList(String role){
        adminListRoles.add(role);
    }


    public void removeMemberFromWhiteList(String member){
        whiteListMembers.remove(member);
    }
    public void removeMemberFromBlackList(String member){
        blackListMembers.remove(member);
    }
    public void removeMemberFromAdminList(String member){
        adminListMembers.remove(member);
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
    public void removeRoleFromAdminList(String role){
        adminListRoles.remove(role);
    }



    private boolean isMemberWhite (String member){
        return whiteListMembers.contains(member);
    }
    private boolean isMemberBlack (String member){
        return blackListMembers.contains(member);
    }
    private boolean isMemberAdmin (String member){
        return adminListMembers.contains(member);
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
    private boolean isRoleAdmin (String role){
        return adminListRoles.contains(role);
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
        File admins=new File("admins");

        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(admins));

            objectOutputStream.writeObject(adminListMembers);
            objectOutputStream.writeObject(adminListRoles);

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
        File admins=new File("admins");
        if (admins.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(admins));

                adminListMembers=(ArrayList<String>) objectInputStream.readObject();
                adminListRoles=(ArrayList<String>) objectInputStream.readObject();

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

    private Program getProgramByTag(String programTag) {
        for (Program program:programs)
            if (program.programTag.equals(programTag))
                return program;
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
            if (availableResource.resources.remove(resourceToRemove)) {
                return "Resource removed.";
            }
        return "Resource not found.";
    }
    private String handleAddMessage(String message) {
        String messageData=replaceFirst(message,".add_message ", "");
        if (!(messageData.startsWith("[")&&messageData.contains("]")))
            return "invalid category";
        String messageCatToFind=split(messageData,"]")[0]+']';

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
    private String handleAddResponse(String message) {
        String messageData=replaceFirst(message,".add_response ", "");
        if (!(messageData.startsWith("[")&&messageData.contains("]")))
            return "invalid category";
        String messageCatToFind=split(messageData,"]")[0]+']';

        String messageContent=replaceFirst(messageData,messageCatToFind+" ","");

        MessageCat messageCat=getMessageCatByTag(messageCatToFind);
        if (messageCat!=null) {
            messageCat.responds.add(getResTagsFromResponseString(messageContent));
            return "Response added";
        }
        messageCat=new MessageCat();
        messageCat.messageTag=messageCatToFind;
        messageCat.responds.add(getResTagsFromResponseString(messageContent));
        messageCats.add(messageCat);
        return "Response added";
    }

    private String handleAddProgram (String message) {
        String programData=replaceFirst(message,".add_program ", "");
        String programTag=split(programData," ")[0];

        String code=replaceFirst(programData,programTag+" ","");

        Program program=getProgramByTag(programTag);
        if (program!=null) {
            return "Program already exists";
        }
        if (JavaStringCompiler.compileString(code,programTag.replace("~",""))) {
            program=new Program();
            program.programTag=programTag;
            program.code=code;
            programs.add(program);
            return "Program added";
        }
        return "Invalid program";

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

    private ArrayList<String>[] getResTagsFromResponseString (String message) {
        String[] programParts=split(" "+message,"~");

        for (int i=1;i<programParts.length;i+=2) {
            message=message.replace("~"+programParts[i]+"~","<"+programParts[i].replace("<","~").replace(">","~")+">");
        }
        ArrayList<String> resTags=new ArrayList<String>();
        ArrayList<String> resTagIDs=new ArrayList<String>();
        String[] messageParts=split(message,"<");
        for (int i=0;i<messageParts.length;i++)
            if (messageParts[i].contains(">")) {
                String resTag="<"+split(messageParts[i],">")[0]+">";
                messageParts[i]=replaceFirst(("<"+messageParts[i]),resTag,"");

                if (resTag.contains("~")) {
                    String[] programArgs = split(" " + resTag, "~");

                    for (int j = 1; j < programArgs.length; j += 2) {
                        resTag = resTag.replace("~" + programArgs[j] + "~", "<" + programArgs[j].replace("<", "~").replace(">", "~") + ">");
                    }

                    resTag=replaceLast(replaceFirst(resTag,"<","~"),">","~");
                    resTagIDs.add("program");
                } else {
                    if (messageParts[i].startsWith("{")) {
                        String res_num = replaceFirst(split(messageParts[i], "}")[0], "{", "");
                        resTagIDs.add(res_num);
                        messageParts[i] = replaceFirst(messageParts[i], "{" + res_num + "}", "");
                    } else {
                        resTagIDs.add(null);
                    }
                }
                resTags.add(resTag);
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
        if (tag.equals("<*>"))
            return true;
        AvailableResource availableResource=getResourceByTag(tag);
        if (availableResource==null)
            return false;
        for (String res:availableResource.resources)
            if (res.equals(resource))
                return true;
        return false;
    }

    private void sendMemberResourceList(User user) {
        String resourceList="Resource list:\n";
        for (int i=0;i<availableResources.size();i++) {
            if (resourceList.length()+availableResources.get(i).resourceTag.length()>400) {
                user.sendMessage(resourceList);
                resourceList=availableResources.get(i).resourceTag+"\n";
            } else {
                resourceList+=availableResources.get(i).resourceTag+"\n";
            }
        }
        user.sendMessage(resourceList);
    }

    private void sendMemberResource(User user, AvailableResource availableResource) {
        String resourceList="Resource " + availableResource.resourceTag + " :\n";
        for (int i=0;i<availableResource.resources.size();i++) {
            if (resourceList.length()+availableResource.resources.get(i).length()>400) {
                user.sendMessage(resourceList);
                resourceList=availableResource.resources.get(i)+"\n";
            } else {
                resourceList+=availableResource.resources.get(i)+"\n";
            }
        }
        user.sendMessage(resourceList);
    }

    private void sendMemberCategoryList(User user) {
        String categoryList="Category list:\n";
        for (int i=0;i<messageCats.size();i++) {
            if (categoryList.length()+messageCats.get(i).messageTag.length()>400) {
                user.sendMessage(categoryList);
                categoryList=messageCats.get(i).messageTag+"\n";
            } else {
                categoryList+=messageCats.get(i).messageTag+"\n";
            }
        }
        user.sendMessage(categoryList);
    }

    LinkedList<String>[] extractResources(ArrayList<String>[] messageStructure, int index, String message) {
        String startingConstant = messageStructure[0].get(index);
        if (startingConstant.length() != 0)
            if (message.startsWith(startingConstant))
                message = replaceFirst(message, messageStructure[0].get(index), "");
            else
                return null;//structure didn't fit
        if (index == messageStructure[1].size()) {
            if (message.length()==0) {
                LinkedList<String>[] ret = new LinkedList[3];
                ret[0] = new LinkedList<String>();
                ret[1] = new LinkedList<String>();
                ret[2] = new LinkedList<String>();
                return ret;
            } else
                return null;
        }
        String[] messageParts;
        String nextStructureConstant=messageStructure[0].get(index+1);
        //split the text at next constant occurrence
        if (nextStructureConstant.length() != 0)
            messageParts = split(message,nextStructureConstant);
        else {
            messageParts=message.split("");
        }
        String resFound="";
        //search for resource
        for (int k = 0; k < messageParts.length; k++) {
            resFound += messageParts[k];

            if (containsRes(messageStructure[1].get(index), resFound)) {//fits so far
                LinkedList<String>[] resourcesFoundSoFar=extractResources(messageStructure, index + 1, replaceFirst(message, resFound,""));
                if (resourcesFoundSoFar!=null) {
                    resourcesFoundSoFar[0].addFirst(resFound);
                    resourcesFoundSoFar[1].addFirst(messageStructure[1].get(index));
                    resourcesFoundSoFar[2].addFirst(messageStructure[2].get(index));
                    return resourcesFoundSoFar;
                }
            }

            resFound += nextStructureConstant;
        }

        return null;
    }

    private String readMessageGenerateResponse (String message, String senderMentionTag, String sendChannel) {
        String response = null;
        message=message.replaceAll(mentionTag,"@me");
        //for each message category
        NextCat:for (MessageCat messageCat : messageCats)
            //for each message structure
            NextMessage:for (ArrayList<String>[] messageStructure : messageCat.messages) {
                LinkedList<String>[] foundResources=extractResources(messageStructure,0,message);
                if (foundResources==null)
                    continue NextMessage;
                if (messageCat.responds.size()==0) {
                    response = messageCat.messageTag;
                    return response;
                }
                response="";
                Random rnd = new Random();
                ArrayList<String>[] responseStructure = messageCat.responds.get(rnd.nextInt()%messageCat.responds.size());

                NextStructure:for (int i=0;i<responseStructure[0].size();i++){
                    response=response+responseStructure[0].get(i);
                    //add resource
                    if (i<responseStructure[2].size()) {
                        if (!responseStructure[1].get(i).startsWith("~")) {
                            AvailableResource currentResource = getResourceByTag(responseStructure[1].get(i));

                            if (responseStructure[2].get(i) == null) {
                                if (currentResource == null || currentResource.resources.size() == 0) {
                                    response = response + "(Invalid Resource)";
                                    continue;
                                }
                                response = response + currentResource.resources.get(rnd.nextInt() % currentResource.resources.size());
                            } else {
                                for (int j = 0; j < foundResources[0].size(); j++)
                                    if (foundResources[2].get(j).equals(responseStructure[2].get(i)) && foundResources[1].get(j).equals(responseStructure[1].get(i))) {
                                        response = response + foundResources[0].get(j);
                                        continue NextStructure;
                                    }
                                if (currentResource == null || currentResource.resources.size() == 0) {
                                    response = response + "(Invalid Resource)";
                                    continue;
                                }
                                response = response + currentResource.resources.get(rnd.nextInt() % currentResource.resources.size());
                            }
                        } else {
                            ArrayList<String>[] programArgs = getResTagsFromString(responseStructure[1].get(i));
                            String[] args = new String[programArgs[1].size()];

                            for (int k=0;k<args.length;k++) {
                                String arg = null;
                                for (int j=0;j<args.length;j++)
                                    if (foundResources[1].get(j).equals(programArgs[1].get(k))&&foundResources[2].get(j).equals(programArgs[2].get(k)))
                                        arg=foundResources[0].get(j);

                                if (arg==null) {
                                    response = response + "(Invalid Resource For Program)";
                                    break;
                                }

                                args[k]=arg;

                                if (k==(args.length-1)) {
                                    String[] methodParts = split(programArgs[0].get(0),".");
                                    if (getProgramByTag(methodParts[0]+"~")==null)
                                        response = response + "(Invalid Resource For Program)";
                                    else {
                                        response = response + runProgram(methodParts[1],split(methodParts[2],"(")[0],args);
                                    }
                                }
                            }
                        }
                    }
                }
                response = response.replace("@sender", senderMentionTag);
                response = response.replace("#sent-channel", sendChannel);
                response = response.replace("@me", mentionTag);
                return response;
            }

        return response;
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
        programs = new ArrayList<Program>();
    }

    public void handleMessages(final Message message, final boolean isWhite, final boolean isAdmin){
        String messageContent = message.getContent();
        while (messageContent.endsWith(" "))
            messageContent = replaceLast(messageContent, " ", "");
        messageContent = messageContent.toLowerCase();
        if ((messageContent.startsWith(".add_program ")) && isWhite)
            message.reply(handleAddProgram(message.getContent()));
        else if (messageContent.startsWith(".new_res ") && isWhite)
            message.reply(handleAddNewRes(messageContent));
        else if ((messageContent.startsWith(".add_to_res ")) && isWhite)
            message.reply(handleAddToRes(messageContent));
        else if ((messageContent.startsWith(".remove_from_res ")) && isWhite)
            message.reply(handleRemoveFromRes(messageContent));
        else if ((messageContent.startsWith(".add_message ")) && isWhite)
            message.reply(handleAddMessage(messageContent));
        else if ((messageContent.startsWith(".add_response ")) && isWhite)
            message.reply(handleAddResponse(messageContent));
        else if (messageContent.equals(".res_list") && isWhite)
            sendMemberResourceList(message.getAuthor());
        else if (messageContent.equals(".cat_list") && isWhite)
            sendMemberCategoryList(message.getAuthor());
        else if (messageContent.startsWith(".res_view ") && isWhite) {
            AvailableResource availableResource=getResourceByTag(replaceFirst(messageContent,".res_view ",""));
            if (availableResource==null)
                message.reply("resource not found");
            else
                sendMemberResource(message.getAuthor(),availableResource);
        } else if (messageContent.equals("?help") && isWhite) {
            new Thread() {
                @Override
                public void run() {
                    message.getChannelReceiver().sendMessage("see also:```?help admin``` ```?help list```\nadd a new resource ```.new_res <resource-tag>```\nadd something to resource ```.add_to_res <resource-tag> res```\nremove something from resource ```.remove_from_res <resource-tag> res```");
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.getChannelReceiver().sendMessage("\n\nadd a message to category```.add_message [category] message content(can include any of the tags below)```\nadd a response message to category```.add_response [category] message content(can include any of the tags below```\n\n*Tags:*\n\nplaceholder for resource");
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.getChannelReceiver().sendMessage("```<resource-tag>{resource-num}```\nusing```{resource-num}```is optional, use it if you want to use the same resource in response\n\nplaceholder for sender of message```@sender```\nplaceholder for channel name```#sent-channel```\nplaceholder for bot```@me```");
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.getChannelReceiver().sendMessage("place holder for arbitrary string```<*>{resource-num}```\nusing```{resource-num}```is optional, use it if you want to use the same resource in response");
                }
            }.start();
        } else if (messageContent.equals("?help admin") && isWhite && isAdmin) {
            new Thread() {
                @Override
                public void run() {
                    message.getChannelReceiver().sendMessage("add a member/channel/role to white list: ```.add white member/channel/role name```\nadd a member/channel/role to black list: ```.add black member/channel/role name```");
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.getChannelReceiver().sendMessage("remove a member/channel/role from white list: ```.remove white member/channel/role name```\nremove a member/channel/role from black list: ```.remove black member/channel/role name```");
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.getChannelReceiver().sendMessage("\nremove a member/role from admin list: ```.remove admin member/role name```\nremove a member/role from admin list: ```.remove admin member/role name```");
                }
            }.start();
        } else if (messageContent.equals("?help list") && isWhite) {
            message.getChannelReceiver().sendMessage("see a list of resources ```.res_list```\nview a single resource ```.res_view <resource-tag>```\nsee a list of categories ```.cat_list```");
        } else if (messageContent.startsWith(".save db ") && isWhite) {
            File db = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".save db ", ""));
            File program = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".save db ", "")+".program");

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

                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(program));
                    objectOutputStream.writeObject(programs);
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                message.reply("access denied");
        } else if (messageContent.startsWith(".load db ") && isWhite) {
            File db = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".load db ", ""));
            File program = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".load db ", "")+".program");

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

                if (program.exists()) {
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(program));
                        programs = (ArrayList<Program>) objectInputStream.readObject();
                        for (Program programAdded:programs)
                            JavaStringCompiler.compileString(programAdded.code,programAdded.programTag.replace("~",""));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    programs = new ArrayList<Program>();
                }
            } else
                message.reply("access denied");
        } else if (messageContent.startsWith(".add db ") && isWhite) {
            File db = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".add db ", ""));
            File program = new File("F:\\IYPBot Database\\" + replaceFirst(messageContent, ".add db ", "")+".program");

            if (!db.getAbsolutePath().contains("..")) {
                if (db.exists()) {
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(db));
                        ArrayList<AvailableResource> availableResourcesToAdd = (ArrayList<AvailableResource>) objectInputStream.readObject();
                        ArrayList<MessageCat> messageCatsToAdd = (ArrayList<MessageCat>) objectInputStream.readObject();


                        //add the resources
                        for (AvailableResource availableResource:availableResourcesToAdd){
                            AvailableResource existingRes=getResourceByTag(availableResource.resourceTag);
                            if (existingRes!=null) {
                                for (String newRes:availableResource.resources)
                                    if (!existingRes.resources.contains(newRes))
                                        existingRes.resources.add(newRes);

                            } else
                                availableResources.add(availableResource);
                        }

                        //add categories
                        for (MessageCat messageCat:messageCatsToAdd) {
                            MessageCat existingMessageCat=getMessageCatByTag(messageCat.messageTag);
                            if (existingMessageCat!=null) {
                                for (ArrayList<String>[] messageStructure : messageCat.messages)
                                    if (!existingMessageCat.messages.contains(messageStructure))
                                        existingMessageCat.messages.add(messageStructure);
                                for (ArrayList<String>[] responseStructure : messageCat.responds)
                                    if (!existingMessageCat.responds.contains(responseStructure))
                                        existingMessageCat.responds.add(responseStructure);
                            } else
                                messageCats.add(messageCat);
                        }


                        message.reply("database loaded");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    message.reply("database not found");

                if (program.exists()) {
                    ObjectInputStream objectInputStream = null;
                    try {
                        objectInputStream = new ObjectInputStream(new FileInputStream(program));
                        ArrayList<Program> programsToAdd = (ArrayList<Program>) objectInputStream.readObject();

                        for (Program programToAdd:programsToAdd)
                            if (getProgramByTag(programToAdd.programTag)==null) {
                                JavaStringCompiler.compileString(programToAdd.code,programToAdd.programTag.replace("~",""));
                                programs.add(programToAdd);
                            }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else
                message.reply("access denied");
        } else if (messageContent.equals(".reset")&&isWhite) {
            resetDatabase();
            message.reply("database loaded");
            
        }else if (messageContent.startsWith(".add admin member ")&&isWhite && isAdmin) {
            addMemberToAdminList(replaceFirst(messageContent,".add admin member ",""));
            message.reply("member added to admin list.");
            savePermissions();

        }else if (messageContent.startsWith(".add white member ")&&isWhite && isAdmin) {
            addMemberToWhiteList(replaceFirst(messageContent,".add white member ",""));
            message.reply("member added to white list.");
            savePermissions();

        }else if (messageContent.startsWith(".add black member ")&&isWhite && isAdmin) {
            addMemberToBlackList(replaceFirst(messageContent,".add black member ",""));
            message.reply("member added to black list.");
            savePermissions();



        }else if (messageContent.startsWith(".add white channel ")&&isWhite && isAdmin) {
            addChannelToWhiteList(replaceFirst(messageContent,".add white channel ",""));
            message.reply("channel added to white list.");
            savePermissions();

        }else if (messageContent.startsWith(".add black channel ")&&isWhite && isAdmin) {
            addChannelToBlackList(replaceFirst(messageContent,".add black channel ",""));
            message.reply("channel added to black list.");
            savePermissions();



        }else if (messageContent.startsWith(".add admin role ")&&isWhite && isAdmin) {
            addRoleToAdminList(replaceFirst(messageContent,".add admin role ",""));
            message.reply("role added to admin list.");
            savePermissions();

        }else if (messageContent.startsWith(".add white role ")&&isWhite && isAdmin) {
            addRoleToWhiteList(replaceFirst(messageContent,".add white role ",""));
            message.reply("role added to white list.");
            savePermissions();

        }else if (messageContent.startsWith(".add black role ")&&isWhite && isAdmin) {
            addRoleToBlackList(replaceFirst(messageContent,".add black role ",""));
            message.reply("role added to black list.");
            savePermissions();


            
            
            
            
        }else if (messageContent.startsWith(".remove admin member ")&&isWhite && isAdmin) {
            removeMemberFromAdminList(replaceFirst(messageContent,".remove admin member ",""));
            message.reply("member removed from admin list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove white member ")&&isWhite && isAdmin) {
            removeMemberFromWhiteList(replaceFirst(messageContent,".remove white member ",""));
            message.reply("member removed from white list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove black member ")&&isWhite && isAdmin) {
            removeMemberFromBlackList(replaceFirst(messageContent,".remove black member ",""));
            message.reply("member removed from black list.");
            savePermissions();



        }else if (messageContent.startsWith(".remove white channel ")&&isWhite && isAdmin) {
            removeChannelFromWhiteList(replaceFirst(messageContent,".remove white channel ",""));
            message.reply("channel removed from white list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove black channel ")&&isWhite && isAdmin) {
            removeChannelFromBlackList(replaceFirst(messageContent,".remove black channel ",""));
            message.reply("channel removed from black list.");
            savePermissions();



        }else if (messageContent.startsWith(".remove admin role ")&&isWhite && isAdmin) {
            removeRoleFromAdminList(replaceFirst(messageContent,".remove admin role ",""));
            message.reply("role removed from admin list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove white role ")&&isWhite && isAdmin) {
            removeRoleFromWhiteList(replaceFirst(messageContent,".remove white role ",""));
            message.reply("role removed from white list.");
            savePermissions();

        }else if (messageContent.startsWith(".remove black role ")&&isWhite && isAdmin) {
            removeRoleFromBlackList(replaceFirst(messageContent,".remove black role ",""));
            message.reply("role removed from black list.");
            savePermissions();


        }else {
            String response = readMessageGenerateResponse(messageContent, message.getAuthor().getMentionTag(), message.getChannelReceiver().getMentionTag());
            if (response != null)
                message.reply(response);
        }

    }

    public ProgrammableBot(String token) {
        final DiscordAPI api = Javacord.getApi(token, true);
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
                            boolean isAdmin=isMemberAdmin(memberName)||isRoleAdmin(roleName);
                            handleMessages(message,isWhite,isAdmin);
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
