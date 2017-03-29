package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Message;

public class LogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = (TableLayout)findViewById(R.id.messagesTable);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Message> messages = Select.from(Message.class).orderBy("a DESC").list();
        for (Message message : messages) {
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
            TextView valueName = (TextView)tableRow.findViewById(R.id.dataName);
            Date date = new Date(message.getTime());
            valueName.setText(dateFormat.format(date));

            TextView valueValue = (TextView)tableRow.findViewById(R.id.dataValue);
            valueValue.setText(message.getMessage());
            statTable.addView(tableRow);
        }
    }

}
