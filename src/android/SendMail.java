package com.autentia.plugin.sendmail;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

public class SendMail extends CordovaPlugin {

	public static final String ACTION_SEND = "send";

	public boolean execute(String action, JSONArray jsonArgs, final CallbackContext callbackContext) throws JSONException {
		if (ACTION_SEND.equals(action)) {
			// Get the json arguments as final for thread usage.
			final JSONObject args = jsonArgs.getJSONObject(0);

			// Run in a thread to not block the webcore thread.
			cordova.getThreadPool().execute(new Runnable() {
				// Thread method.
				public void run() {
					// Try to send the the mail.
					try {
						// Get the arguments.

						// ServerInfo
						String host = args.getString("host");
						String sender = args.getString("sender");
						String password = args.getString("password");
						String port = args.getString("port");

						// Mail info
						String subject = args.getString("subject");
						String body = args.getString("body");
						String recipients = args.getString("recipients");

						// Init attachment list.
						List<Attachment> attachments = new ArrayList<Attachment>();
						
						if (args.has("attachment")) {
							JSONArray jArray = new JSONArray(args.getString("attachment"));
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jObj = jArray.getJSONObject(i);
								String fileName = jObj.getString("fileName");
								String base64Source = jObj.getString("base64Source");

								Attachment attachment = new Attachment(fileName, base64Source);
								if(attachment.getBase64Source() == null || attachment.getBase64Source().equals("")) continue;
								attachments.add(attachment);
							}
						}
						// Create the sender
						SameMailSender sameMailSender = new SameMailSender(host, sender, password, port);

						// Send the mail.
						sameMailSender.sendMail(recipients, sender, subject, body, attachments);

						// Thread safe callback.
						callbackContext.success("Send Success!");
					} catch (Exception e) {
						// Catch error.
						callbackContext.error(e.getMessage() + "\n" + e.toString() + "\n" + stackTraceToString(e));
					}
				}
			});
			return true;
		}
		return false;
	}

	private String stackTraceToString(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
}
