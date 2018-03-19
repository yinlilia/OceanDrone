package oceandrone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.idst.nls.NlsClient;
import com.alibaba.idst.nls.NlsListener;
import com.alibaba.idst.nls.StageListener;
import com.alibaba.idst.nls.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.internal.protocol.NlsRequestProto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by yinlili on 2017/12/9.
 */


public class OceanDroneInterface extends Activity {
    private static final String VOICE_STATE = "voiceButton";
    private static final String SERVICE_STATE = "isResponse";
    private static final int REQUEST_RECORD_AUDIO_CODE = 100;
    private static final String[] BACK = {"返航", "反黄", "泛黄", "敢行", "感行", "繁忙", "导航", "粉红"};
    private static final String[] STOP = {"停止", "停滞", "挺直", "停职", "挺值"};
    private static final String[] START = {"开始", "开市", "开示", "凯时", "开食", "开释", "揩拭",};
    private static final String[] BS = {"反推", "饭退", "返退", "退", "反退", "嗯退"};
    private static final String[] DETOUR = {"绕开", "召开"};
    private static final int DELAY_TIME = 5000;
    private static final String SP_FILE_NAME = "config";


    private ZzHorizontalProgressBar2 p1;
    private ZzHorizontalProgressBar3 p2;
    private ZzHorizontalProgressBar p3;
    private HashMap<String, String> hashMap;
    private static int command;
    private Handler handler;

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private MyHandler myHandler;
    /*@BindView(R.id.voice) ImageButton micro;*/
    private Context context;
    private NlsClient mNlsClient;
    private NlsRequest mNlsRequest;
    /*private ViewStub vs_hint;
    private ViewStub vs_image;*/

    /*@BindView(R.id.start_back)  ImageView reminder1_Image;
    @BindView(R.id.textView1) TextView reminder1_Text;
    @BindView(R.id.suspend)  ImageView reminder2_Image;
    @BindView(R.id.textView2) TextView reminder2_Text;
    @BindView(R.id.back) ImageView iv_back;
    @BindView(R.id.textView3) TextView tv_back;
    @BindView(R.id.stop) ImageView iv_stop;
    @BindView(R.id.textView4) TextView tv_stop;
    @BindView(R.id.start) ImageView iv_start;
    @BindView(R.id.textView5) TextView tv_start;
    @BindView(R.id.detour) ImageView iv_detour;
    @BindView(R.id.textView6) TextView tv_detour;
    @BindView(R.id.backStepping) ImageView iv_bs;
    @BindView(R.id.textView7) TextView tv_bs;
    @BindView(R.id.textView10) TextView tv_speed;
    @BindView(R.id.textView8) TextView tv_angle;
    @BindView(R.id.textView9) TextView tv_communication;*/
    /*@BindView(R.id.item_image) ViewStub vs_image;
    @BindView(R.id.item_hint) ViewStub vs_hint;*/
    private ImageView reminder1_Image;
    private TextView reminder1_Text;
    private TextView reminder2_Text;
    private ImageView reminder2_Image;
    private ImageView iv_back;
    private ImageView iv_stop;
    private TextView tv_back;
    private TextView tv_stop;
    private ImageView iv_start;
    private TextView tv_start;
    private ImageView iv_detour;
    private ImageView iv_bs;
    private TextView tv_bs;
    private TextView tv_speed;
    private TextView tv_angle;
    private TextView tv_communication;
    private TextView tv_detour;
    private ViewStub vs_hint;
    private ViewStub vs_image;
    private ImageButton micro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocean_drone_interface);
        ButterKnife.bind(this);
        context = getApplicationContext();
        String appkey = "nls-service-care"; //请设置申请到的Appkey
        sp = getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE);
        edit=sp.edit();
        edit.apply();
        mNlsRequest = initNlsRequest();
        mNlsRequest.setApp_key(appkey);    //appkey请从 "快速开始" 帮助页面的appkey列表中获取
        mNlsRequest.setAsr_sc("opu");//设置语音格式
        vs_hint=findViewById(R.id.item_hint);
        vs_image=findViewById(R.id.item_image);

        vs_hint.inflate();
        vs_image.inflate();

        /*r1 = findViewById(R.id.hint);
        mhint = findViewById(R.id.back);*/
        reminder1_Image = findViewById(R.id.start_back);
        reminder2_Image = findViewById(R.id.suspend);
        reminder1_Text = findViewById(R.id.textView1);
        reminder2_Text = findViewById(R.id.textView2);
        iv_back = findViewById(R.id.back);
        tv_back = findViewById(R.id.textView3);
        iv_start = findViewById(R.id.start);
        tv_start = findViewById(R.id.textView5);
        iv_stop = findViewById(R.id.stop);
        tv_stop = findViewById(R.id.textView4);
        iv_detour = findViewById(R.id.detour);
        tv_detour = findViewById(R.id.textView6);
        iv_bs = findViewById(R.id.backStepping);
        tv_bs = findViewById(R.id.textView7);
        tv_angle = findViewById(R.id.textView8);
        tv_speed = findViewById(R.id.textView10);
        tv_communication = findViewById(R.id.textView9);
        micro=findViewById(R.id.voice);


        p1 = findViewById(R.id.progressBar1);
        p2 = findViewById(R.id.progressBar2);
        p3 = findViewById(R.id.progressBar3);
        //Button btn_numberProgressBar = (Button) findViewById(R.id.btn_numberProgressBar);

        /*设置热词相关属性*//*
        */

        mNlsRequest.setAsrVocabularyId("vocabid");


        /*设置热词相关属性*//*
        */


        NlsClient.openLog(true);
        NlsClient.configure(getApplicationContext()); //全局配置
        mNlsClient = NlsClient.newInstance(this, mRecognizeListener, mStageListener, mNlsRequest);                          //实例化NlsClient

        mNlsClient.setMaxRecordTime(60000);  //设置最长语音
        mNlsClient.setMaxStallTime(1000);    //设置最短语音
        mNlsClient.setMinRecordTime(500);    //设置最大录音中断时间
        mNlsClient.setRecordAutoStop(false);  //设置VAD
        mNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长

        hashMap = new HashMap<>();
        myHandler = new MyHandler();
        initEvent();
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(timerTask1, 0, 5000);



        /*if (hashMap != null) {
            timer2 = new Timer();
            timer2.scheduleAtFixedRate(timerTask2, 0, 5000);
        }*/
    }



    public void initEvent() {
        micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.getBoolean(VOICE_STATE, false)) {
                    closeVoice();
                    edit.putBoolean(VOICE_STATE, false);
                } else if (checkRecordPermission()) {
                    openVoice();
                    edit.putBoolean(VOICE_STATE, true);
                } else if (sp.getBoolean(SERVICE_STATE, true)) {
                    handler.post(upUIRunable2);
                }
                edit.apply();
            }
        });

    }




    private NlsRequest initNlsRequest() {
        NlsRequestProto proto = new NlsRequestProto(context);
        //proto.setApp_user_id(""); //设置在应用中的用户名，可选
        return new NlsRequest(proto);

    }

    private void initStartRecognizing() {
        mNlsRequest.authorize("LTAIq6UYT6HVyDpw", "YCQIjNJujPiVRERF5W2eorpELYnlXI"); //请替换为用户申请到的数加认证key和密钥
        mNlsClient.start();
    }

    private void initStopRecognizing() {
        //mResultEdit.setText("");
        mNlsClient.stop();


    }

    private NlsListener mRecognizeListener = new NlsListener() {

        @Override
        public void onRecognizingResult(int status, RecognizedResult result) {
            switch (status) {
                case NlsClient.ErrorCode.SUCCESS:
                    Log.i("asr", "[demo]  callback onRecognizResult " + result.asr_out);
                    int beginIndex = result.asr_out.indexOf("t") + 4;
                    int endIndex = result.asr_out.indexOf("i") - 4;
                    String str = result.asr_out.substring(beginIndex, endIndex);
                    Log.d("检测到命令", str);
                    if (contains(BACK, str)) {
                        iv_back.setBackgroundResource(R.drawable.hint);
                        tv_back.setText("返航");
                        command = 11;
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                iv_back.setBackgroundResource(0);
                                tv_back.setText("");
                            }
                        }, DELAY_TIME);
                    } else if (contains(STOP, str)) {
                        command = 4;
                        iv_stop.setBackgroundResource(R.drawable.hint);
                        tv_stop.setText("停止");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                iv_stop.setBackgroundResource(0);
                                tv_stop.setText("");
                            }
                        }, DELAY_TIME);
                    } else if (contains(START, str)) {
                        command = 10;
                        iv_start.setBackgroundResource(R.drawable.hint);
                        tv_start.setText("开始");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                iv_start.setBackgroundResource(0);
                                tv_start.setText("");
                            }
                        }, DELAY_TIME);
                    } else if (contains(DETOUR, str)) {
                        command = 12;
                        iv_detour.setBackgroundResource(R.drawable.hint);
                        tv_detour.setText("绕开");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                iv_detour.setBackgroundResource(0);
                                tv_detour.setText("");
                            }
                        }, DELAY_TIME);
                    } else if (contains(BS, str)) {
                        command = 13;
                        iv_bs.setBackgroundResource(R.drawable.hint);
                        tv_bs.setText("反推");
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                iv_bs.setBackgroundResource(0);
                                tv_bs.setText("");
                            }
                        }, DELAY_TIME);
                    } else {
                        Toast.makeText(OceanDroneInterface.this, "不是指定的指令", Toast.LENGTH_LONG).show();
                    }
                    Thread thread=new Thread(network);
                    thread.start();
                    break;
                case NlsClient.ErrorCode.RECOGNIZE_ERROR:
                    Toast.makeText(OceanDroneInterface.this, "recognizer error", Toast.LENGTH_LONG).show();
                    break;
                case NlsClient.ErrorCode.RECORDING_ERROR:
                    Toast.makeText(OceanDroneInterface.this, "recording error", Toast.LENGTH_LONG).show();
                    break;
                case NlsClient.ErrorCode.NOTHING:
                    Toast.makeText(OceanDroneInterface.this, "nothing", Toast.LENGTH_LONG).show();
                    break;
            }
        }

    };
    private StageListener mStageListener = new StageListener() {
        @Override
        public void onStartRecognizing(NlsClient recognizer) {
            super.onStartRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onStopRecognizing(NlsClient recognizer) {
            super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onStartRecording(NlsClient recognizer) {
            super.onStartRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onStopRecording(NlsClient recognizer) {
            super.onStopRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onVoiceVolume(int volume) {
            super.onVoiceVolume(volume);
        }

    };

    /**
     * 打开录音
     */
    private void openVoice() {
        initStartRecognizing();
        micro.setBackgroundResource(R.drawable.microphone2);  //不在录音则更换为录音按钮
        //voiceButton.setVisibility(View.VISIBLE);
        //etRelativeLayout.setVisibility(View.GONE);
    }


    /**
     * 关闭录音
     */
    private void closeVoice() {
        micro.setBackgroundResource(R.drawable.microphone);  //在录音则更换为键盘按钮
        initStopRecognizing();

    }

    private boolean checkRecordPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager
                    .PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_CODE);
            return false;
        }
        return true;
    }

    public static boolean contains(String[] stringArray, String source) {
        // 转换为list
        List<String> tempList = Arrays.asList(stringArray);
        Boolean opinion=false;
        // 利用list的包含方法,进行判断
        if (tempList.contains(source))
            opinion = true;
        return opinion;
    }


    public static String readParse(String urlPath) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len ;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        return new String(outStream.toByteArray());//通过out.Stream.toByteArray获取到写的数据


        /*BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        HttpURLConnection connection = null;

        try{

        URL url1 = new URL(urlPath);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String s = null;
        while ((s = br.readLine()) != null){
            sb.append(s);
        }
        //Log.e(TAG, "getUrlData: sb1--"+sb.toString() );
        br.close();
    }catch (Exception e){
        e.printStackTrace();
    }finally {
        if (connection != null){
            connection.disconnect();
        }
    }
    return sb.toString();*/

    }

    private static HashMap<String, String> analysis(String jsonStr)
            throws JSONException {

/******************* 解析 ***********************/

        JSONObject jsonObject ;
        // 初始化list数组对象
        HashMap<String, String> map = new HashMap<>();
        /*ArrayList<HashMap<String, Object>> list = new ArrayList<>();*/
        jsonObject = new JSONObject(jsonStr);
        System.out.print(jsonObject);
        for (int i = 0; i < jsonObject.length(); i++) {
            // 初始化map数组对象
            map.put("speed", jsonObject.getString("speed"));
            map.put("angle", jsonObject.getString("angle"));
            map.put("communicate", jsonObject.getString("communicate"));
            map.put("time", jsonObject.getString("time"));
            map.put("battery", jsonObject.getString("battery"));
            map.put("radius", jsonObject.getString("radius"));
            //list.add(map);
        }
        return map;
    }

    TimerTask timerTask1 = new TimerTask() {
        @Override
        public void run() {
            try {
                String result = readParse("http://demo.iot.sjtudoit.cn/api/v2/ocean/info_mobile");
                Message message = new Message();
                message.obj = result;
                myHandler.sendMessage(message);
            } catch (Exception e) {
                System.out.println("服务器请求出错");
            }

        }
    };
    /*TimerTask timerTask2 = new TimerTask() {
        @Override
        public void run() {
            speed = hashMap.get("speed");
            communication = hashMap.get("communicate");
            angle = hashMap.get("angle");
            time = hashMap.get("time");
            battery = hashMap.get("battery");
            radius = hashMap.get("radius");
            p1.setProgress(Integer.parseInt(time) / 10);
            p2.setProgress(Integer.parseInt(battery) / 3);
            p3.setProgress(Integer.parseInt(radius) / 3);
        }
    };*/
    Runnable upUIRunable2 = new Runnable() {
        @Override
        public void run() {
            switch (command) {
                case 11:
                    reminder1_Text.setText("√开始返航");
                    reminder1_Image.setBackgroundResource(R.drawable.hint);
                case 4:
                    reminder1_Text.setText("√开始停止");
                    reminder1_Image.setBackgroundResource(R.drawable.hint);
                case 10:
                    reminder1_Text.setText("√开始启航");
                    reminder1_Image.setBackgroundResource(R.drawable.hint);
                case 12:
                    reminder1_Text.setText("√开始绕回");
                    reminder1_Image.setBackgroundResource(R.drawable.hint);
                case 13:
                    reminder1_Text.setText("√开始反推");
                    reminder1_Image.setBackgroundResource(R.drawable.hint);
            }
        }
    };
    Runnable network=new Runnable() {
        @Override
        public void run() {
            //HttpURLConnection connection=null;
            try {
                String spec = "http://demo.iot.sjtudoit.cn/api/v2/ocean/mode?mode="
                        + URLEncoder.encode(String.valueOf(command), "UTF-8");
                // 根据地址创建URL对象(网络访问的url)
                URL url = new URL(spec);
                // url.openConnection()打开网络链接
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setRequestMethod("GET");// 设置请求的方式
                urlConnection.setReadTimeout(5000);// 设置超时的时间
                urlConnection.setConnectTimeout(5000);// 设置链接超时的时间
                // 设置请求的头
                urlConnection
                        .setRequestProperty("User-Agent",
                                "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
                // 获取响应的状态码 404 200 505 302
                if (urlConnection.getResponseCode() == 200) {
                    // 获取响应的输入流对象\
                    Looper.prepare();
                    Message message = new Message();
                    message.obj = true;
                    myHandler.sendMessage(message);
                    edit.putBoolean(SERVICE_STATE, true);
                    edit.apply();

                    Log.d("检测服务器是否响应", "" + sp.getBoolean(SERVICE_STATE, true));

                } else {
                    System.out.println("链接失败");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private  class MyHandler extends Handler {
        private String speed;
        private String angle;
        private String communication;
        private String radius="1";
        private String time="1";
        private String battery="1";
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj.toString().equals("true")) {
                switch (command) {
                    case 11:
                        reminder1_Text.setText("√开始返航");
                        reminder1_Image.setBackgroundResource(R.drawable.back3);
                        break;
                    case 4:
                        reminder2_Text.setText("√开始停止");
                        reminder2_Image.setBackgroundResource(R.drawable.back3);
                        break;
                    case 10:
                        reminder1_Text.setText("√开始启航");
                        reminder1_Image.setBackgroundResource(R.drawable.back3);
                        break;
                    case 12:
                        reminder2_Text.setText("√开始绕回");
                        reminder2_Image.setBackgroundResource(R.drawable.back3);
                        break;
                    case 13:
                        reminder1_Text.setText("√开始反推");
                        reminder1_Image.setBackgroundResource(R.drawable.back3);
                        break;
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        reminder1_Image.setBackgroundResource(0);
                        reminder1_Text.setText("");
                        reminder2_Image.setBackgroundResource(0);
                        reminder2_Text.setText("");
                    }
                }, DELAY_TIME);
                command=0;
            } else if(!msg.obj.toString().equals("false")) {
                try {
                    hashMap = analysis(msg.obj.toString());
                    speed = hashMap.get("speed");
                    communication = hashMap.get("communicate");
                    angle = hashMap.get("angle");
                    time = hashMap.get("time");
                    battery = hashMap.get("battery");
                    radius = hashMap.get("radius");
                    tv_speed.setText(speed);
                    tv_angle.setText(angle);
                    tv_communication.setText(communication);
                    p1.setProgress(Integer.parseInt(time) / 10);
                    p2.setProgress(Integer.parseInt(battery));
                    //p3.setProgress(Integer.parseInt(radius)*1000 / 3000);

                } catch (JSONException e) {
                    System.out.println("解析出错");

                }
            }
        }
    }
}
