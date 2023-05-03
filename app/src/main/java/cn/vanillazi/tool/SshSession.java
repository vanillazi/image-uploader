package cn.vanillazi.tool;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

public class SshSession  implements Closeable {
    private String host;
    private String user;
    private int port;
    private String password;
    private Session session;

    public SshSession(String host,int port,String user,String password){
        this.host=host;
        this.port=port;
        this.password=password;
        this.user=user;
    }

    public void initSession() throws JSchException {
        if(session!=null && session.isConnected()){
            return;
        }
        var jsch = new JSch();
        session = jsch.getSession(user, host, port);
        var config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        //time in millis
        session.connect(5000);
        session.setTimeout(5000);
    }

    public Session nativeSession(){
        return session;
    }

    public ChannelSftp newSftp() throws JSchException {
        var sftp=(ChannelSftp)session.openChannel("sftp");
        sftp.connect();
        return sftp;
    }

    @Override
    public void close() throws IOException {
        session.disconnect();
    }

    public static  SshSession newDefault(){
        return new SshSession("test",22,"root","test");
    }
}
