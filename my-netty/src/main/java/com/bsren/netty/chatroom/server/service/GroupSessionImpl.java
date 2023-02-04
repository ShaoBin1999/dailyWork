package com.bsren.netty.chatroom.server.service;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupSessionImpl implements GroupSession{

    Map<String,Set<String>> groups;


    @Override
    public boolean add(String groupName, String username) {
        if(!groups.containsKey(groupName)){
            return false;
        }
        groups.get(groupName).add(username);
        return true;
    }

    @Override
    public boolean quit(String groupName, String username) {
        Set<String> set = groups.get(groupName);
        if(set!=null){
            set.remove(username);
            return true;
        }else {
            return false;
        }
    }


    @Override
    public boolean create(String groupName, Set<String> usernames) {
        if(groups.containsKey(groupName)){
            return false;
        }
        groups.put(groupName,usernames);
        return true;
    }

    @Override
    public Set<String> getMembers(String groupName) {
        return groups.getOrDefault(groupName, null);
    }

    @Override
    public Map<String, Set<Channel>> getMemberChannels(String groupName) {
        Map<String,Set<Channel>> ret = new HashMap<>();
        if(groups.containsKey(groupName)){
            Set<String> members = groups.get(groupName);
            for (String member : members) {
                Set<Channel> channels = SessionFactory.getSession().getChannel(member);
                ret.put(member,channels);
            }
        }
        return ret;
    }
}
