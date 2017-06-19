package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.Message;

public class LogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.message_log);
        ((TextView)findViewById(R.id.activitySubtitle)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.activitySubtitle)).setText(R.string.log_subtitle);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = findViewById(R.id.dataTable);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Message> messages = Select.from(Message.class).orderBy("a DESC").list();
        for (Message message : messages) {
            statTable.addView(DisplayHelper.getTableRow(inflater, dateFormat.format(new Date(message.getTime())), message.getMessage()));
        }
    }

}
