package uk.ac.cam.teamhotel.historyphone.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.ac.cam.teamhotel.historyphone.R;
import uk.ac.cam.teamhotel.historyphone.artifact.Artifact;
import uk.ac.cam.teamhotel.historyphone.database.ChatMessage;
import uk.ac.cam.teamhotel.historyphone.utils.TimeStampHelper;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    public static final String TAG = "ChatAdapter";

    private Artifact artifact;

    public ChatAdapter(Context context, Artifact artifact, List<ChatMessage> messages) {
        // Resource ID = 0 as custom adapter already handles this.
        super(context, 0, messages);
        this.artifact = artifact;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        // Get the chat message for this position.
        ChatMessage chatMessage = getItem(position);
        if (chatMessage == null) {
            Log.e(TAG, "Null message.");
            return new View(getContext());
        }

        // Get the type of the layout.
        int type = getItemViewType(position);

        // Check if an existing view is being reused, otherwise inflate the view.
        // TODO: Check whether existing views are of the correct type.
        if (view == null) {
            switch (type) {
                case ChatMessage.TYPE_FROM_ARTIFACT:
                    view = LayoutInflater
                            .from(getContext())
                            .inflate(R.layout.list_item_message_reply, parent, false);
                    break;

                case ChatMessage.TYPE_FROM_USER:
                    view = LayoutInflater
                            .from(getContext())
                            .inflate(R.layout.list_item_message_user, parent, false);
                    break;

                default:
                    Log.e(TAG, "Erroneous view type.");
                    return new View(getContext());
            }
        }

        // Find views for data population.
        ImageView idImage = (ImageView) view.findViewById(R.id.id_image);
        TextView messageText = (TextView) view.findViewById(R.id.chat_message);
        TextView timestamp = (TextView) view.findViewById(R.id.timestamp);

        // Populate the data into the template view using the data object.
        // Set parameters of artifact tile.
        messageText.setText(chatMessage.getText());
        timestamp.setText(TimeStampHelper.formatTime(chatMessage.getTimestamp()));

        // Set the user ID image.
        if (type == ChatMessage.TYPE_FROM_ARTIFACT) {
            // Format artifact image.
            Bitmap image = artifact.getPicture();
            if (image != null) {
                // If the image is set within the Artifact object, use it.
                idImage.setImageBitmap(image);
            } else {
                // Otherwise, fall back on the launcher icon.
                idImage.setImageBitmap(BitmapFactory.decodeResource(view.getResources(),
                        R.mipmap.ic_launcher));
            }
        }

        // Return the completed view to render on screen.
        return view;
    }

    /**
     * Overriding this method allows for two custom list_item_nearby layout files.
     */
    @Override
    public int getViewTypeCount() { return 2; }

    /**
     * Overriding this method allows for two custom list_item_nearby layout files.
     */
    @Override
    public int getItemViewType(int position) {
        // Get corresponding chat message.
        ChatMessage chatMessage = getItem(position);
        // Return the type of the layout, depending on whether the message is a sent or received.
        return (chatMessage != null) ? chatMessage.getType() : -1;
    }
}
