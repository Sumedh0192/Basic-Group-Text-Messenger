package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String[] PORT_NUMBERS = {"11108","11112","11116","11120","11124"};
    static final int SERVER_PORT = 10000;
    static Integer messageNumber = 0;
    private final Uri pa2Uri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");



    private Uri buildUri(String scheme, String authority) {

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new MessageReceiver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            Log.e(TAG,"Exception-->  " + e.getMessage());
            Log.e(TAG,"Exception--->  " + e.getStackTrace());
            return;
        }
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */

        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        final Button sendButton = (Button)findViewById(R.id.button4);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText messageText = (EditText) findViewById(R.id.editText1);
                String messageToSend = messageText.getText().toString();
                messageText.setText("");
                TextView remoteTextView = (TextView) findViewById(R.id.textView1);
                remoteTextView.append(messageToSend + "\n");
                ContentValues conVal = new ContentValues();
                conVal.put("key", messageNumber.toString());
                conVal.put("value", messageToSend);
                messageNumber++;
                getContentResolver().insert(pa2Uri, conVal);
                new MessageMultiCast().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, messageToSend, myPort);
            }
        });

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
    }

    private class MessageReceiver extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {

            ServerSocket serverSocket = sockets[0];
            Socket listner = null;
            BufferedReader br = null;
            DataInputStream inputStream = null;
            while(!serverSocket.isClosed()) {
                try {
                    listner = serverSocket.accept();
                    inputStream = new DataInputStream(listner.getInputStream());
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String serverInput, line;
                    serverInput = "";
                    while ((line = br.readLine()) != null) {
                        serverInput += line;
                    }
                    publishProgress(serverInput);
                } catch (IOException ex) {
                    Log.e(TAG,"Error in Receiving the message");
                    Log.e(TAG,"Exception-->  " + ex.getMessage());
                    Log.e(TAG,"Exception--->  " + ex.getStackTrace());
                    return null;
                } finally {
                    try {
                        br.close();
                        inputStream.close();
                        listner.close();
                    } catch (IOException ex) {
                        Log.e(TAG,"Exception-->  " + ex.getMessage());
                        Log.e(TAG,"Exception--->  " + ex.getStackTrace());
                        return null;
                    }
                }
            }
            try {
                serverSocket.close();
            }catch(IOException ex){
                Log.e(TAG,"Exception-->  " + ex.getMessage());
                Log.e(TAG,"Exception--->  " + ex.getStackTrace());
            }
            return null;
        }

        protected void onProgressUpdate(String...strings) {

            String strReceived = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.textView1);
            remoteTextView.append(strReceived + "\n");
            ContentValues conVal = new ContentValues();
            conVal.put("key", messageNumber);
            conVal.put("value", strReceived);
            messageNumber++;
            getContentResolver().insert(pa2Uri, conVal);
            return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    private class MessageMultiCast extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            Socket socket = null;
            OutputStream out = null;
            PrintWriter writer = null;
            for(Integer i = 0; i < PORT_NUMBERS.length; i++) {
                if (!msgs[1].equals(PORT_NUMBERS[i])) {
                    try {
                        String remotePort = PORT_NUMBERS[i];
                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(remotePort));
                        String msgToSend = msgs[0];
                        out = socket.getOutputStream();
                        writer = new PrintWriter(out, true);
                        writer.write(msgToSend);
                        writer.flush();
                    } catch (UnknownHostException e) {
                        Log.e(TAG, "ClientTask UnknownHostException");
                    } catch (IOException e) {
                        Log.e(TAG, "ClientTask socket IOException");
                    } finally {
                        try {
                            writer.close();
                            out.close();
                            socket.close();
                        } catch (IOException ex) {
                            Log.e(TAG,"Exception-->  " + ex.getMessage());
                            Log.e(TAG,"Exception--->  " + ex.getStackTrace());
                        }
                    }
                }
            }
            return null;
        }
    }
}