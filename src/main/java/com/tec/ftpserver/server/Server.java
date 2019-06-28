package com.tec.ftpserver.server;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Server {

    FtpServerFactory serverFactory;
    ListenerFactory listenerFactory;
    ConnectionConfigFactory configFactory;
    UserManager userManager;

    @Autowired
    public Server(CustomUserManager userManager) {
        this.userManager = userManager;
        run();
    }

    public void run() {
        listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2221);

        configFactory = new ConnectionConfigFactory();
        configFactory.setMaxLogins(100);
        configFactory.setMaxThreads(10);

        serverFactory = new FtpServerFactory();
        serverFactory.addListener("default", listenerFactory.createListener());
        serverFactory.setUserManager(userManager);
        serverFactory.setConnectionConfig(configFactory.createConnectionConfig());

        BaseUser baseUser = new BaseUser();
        baseUser.setName("Bruno");
        baseUser.setPassword("123");

        baseUser.setHomeDirectory(System.getProperty("user.home"));
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        authorities.add(new ConcurrentLoginPermission(1,1));
        baseUser.setAuthorities(authorities);


//        try {
//            userManager.save(baseUser);
//        } catch (FtpException e) {
//            e.printStackTrace();
//        }

        FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }

        System.out.println(server.isStopped());
    }

}