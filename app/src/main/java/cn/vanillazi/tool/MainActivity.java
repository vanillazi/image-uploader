package cn.vanillazi.tool;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.PictureSelectorEngine;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.IOException;
import java.util.ArrayList;

import cn.vanillazi.tool.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.upload1.setOnClickListener(e->{uploadPicture(1);});
        binding.upload2.setOnClickListener(e->{uploadPicture(2);});
        binding.upload3.setOnClickListener(e->{uploadPicture(3);});
        binding.upload4.setOnClickListener(e->{uploadPicture(4);});
    }

    public void uploadPicture(int index){
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setCompressEngine(new DefaultCompressFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        var image=result.get(0);
                        var path=image.getCompressPath();
                        doUpload(index,path);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void doUpload(int index, String path) {
        new Thread(()->{
            var session=SshSession.newDefault();
            try {
                session.initSession();
            } catch (JSchException e) {
                showMessage("上传失败:"+e.getMessage());
            }
            try {
                session.newSftp()
                        .put(path,"/usr/share/nginx/html/"+index+".png");
            } catch (SftpException e) {
                throw new RuntimeException(e);
            } catch (JSchException e) {
                showMessage("上传失败:"+e.getMessage());
            }
            try {
                session.close();
            } catch (IOException e) {
                showMessage("上传失败:"+e.getMessage());
            }
            showMessage("上传成功");
        }).start();
    }

    public void showMessage(String text){
        this.runOnUiThread(()->{
            Toast.makeText(this,text,Toast.LENGTH_LONG).show();
        });
    }

}