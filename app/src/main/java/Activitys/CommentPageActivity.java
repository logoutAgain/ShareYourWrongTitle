package Activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.liangzihong.viewpager.R;

import Adapters.CommentInfo;
import Adapters.TitleInfo;
import Application.MyApplication;
import BmobModels.BComment;
import BmobModels.BWrongTitle;
import MyUi.MyListView;
import Presenters.ILoadCommentInfoPresenter;
import Presenters.LoadCommentInfoPresenter;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Liang Zihong on 2018/12/22.
 */

public class CommentPageActivity extends BaseActivity implements ILoadCommentInfoActivity {

    private ILoadCommentInfoPresenter iLoadCommentInfoPresenter;

    private static TitleInfo titleInfo;
    private ImageView photo;
    private TextView content;
    private MyListView myListView;
    private EditText write_edit;
    private Button send_button;



    public static void startCommentPageActivity(Context context, TitleInfo aTitleInfo)
    {
        Intent intent=new Intent(context, CommentPageActivity.class);
        titleInfo = aTitleInfo;
        context.startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_page);


        init();
    }


    private void init(){
        photo = (ImageView) findViewById(R.id.comment_page_photo);
        content = (TextView) findViewById(R.id.comment_page_content);
        myListView = (MyListView)findViewById(R.id.comment_page_MyListView);
        write_edit = (EditText)findViewById(R.id.comment_page_editText);
        send_button = (Button)findViewById(R.id.comment_page_send_button);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WatchPictureActivity.startPictureActivityByInternet(CommentPageActivity.this, titleInfo.getPhotoUrl());
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(CommentPageActivity.this);
                normalDialog.setMessage("确定要发表评论吗");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendComment();
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                // 显示
                normalDialog.show();
            }
        });



        Glide.with(this).load(titleInfo.getPhotoUrl()).into(photo);
        content.setText(titleInfo.getContent());


        // 获取评论列表，然后加载适配器。
        iLoadCommentInfoPresenter = new LoadCommentInfoPresenter(this);
        iLoadCommentInfoPresenter.loadCommentInfo(titleInfo.getTitleId());


    }

    // 发表评论
    private void sendComment()
    {

        final AlertDialog.Builder waitingDialogBuilder =
                new AlertDialog.Builder(this);
        waitingDialogBuilder.setTitle("正在发表评论");
        waitingDialogBuilder.setMessage("请稍候");
        final AlertDialog waitingDialog = waitingDialogBuilder.create();
        waitingDialog.show();

        MyApplication app = (MyApplication)getApplication();
        final BComment bComment = new BComment();
        bComment.setUserId(app.getCurrentUserId());
        bComment.setTitleId(titleInfo.getTitleId());
        bComment.setComment( write_edit.getText()+"");
        bComment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null)
                {
                    Log.e("fuck", "done: 成功" );
                    waitingDialog.dismiss();
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(CommentPageActivity.this);
                    normalDialog.setTitle("系统信息");
                    normalDialog.setMessage("成功发布评论");

                    write_edit.setText("");
                    normalDialog.show();
                    iLoadCommentInfoPresenter.addOnebComment(bComment);
                }
                else
                {
                    Log.e("fuck", "done: 失败" );
                    waitingDialog.dismiss();

                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(CommentPageActivity.this);
                    normalDialog.setTitle("系统信息");
                    normalDialog.setMessage("发布评论失败\n原因："+e.toString());
                    normalDialog.show();
                }
            }
        });

    }


    // 加载评论列表的接口
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setCommentInfoAdapter(ArrayAdapter<CommentInfo> adapter) {
        myListView.setAdapter(adapter);
    }
}
