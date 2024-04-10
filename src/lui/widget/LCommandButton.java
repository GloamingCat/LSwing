package lui.widget;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import lui.container.LContainer;
import lui.dialog.LErrorDialog;
import lui.dialog.LConfirmDialog;
import lui.base.LVocab;
import lui.base.action.LActionManager;
import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LSelectionListener;
import lui.base.serialization.LSerializer;

public class LCommandButton extends LButton {
	private static final long serialVersionUID = 1L;

	public LSerializer projectSerializer = null;
	public String command = null;
	
	public LCommandButton(LContainer parent, String text) {
		super(parent, text);
		onClick = new LSelectionListener() {
			@Override
			public void onSelect(LSelectionEvent event) {
				if (askSave())
					execute(command);
			}
		};
	}

	protected boolean execute(String command) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("cmd.exe", "/c", command);
			builder.directory(new File("."));
			Process process = builder.start();
			StreamGobbler streamGobbler =  new StreamGobbler(process.getInputStream(), System.out::println);
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			int exitCode = process.waitFor();
			if (exitCode == 0)
				return true;
			System.err.println("Program exit with code: " + exitCode);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected boolean askSave() {
		if (projectSerializer == null)
			return true;
		if (LActionManager.getInstance().hasChanges()) {
			LVocab vocab = LVocab.instance;
			LConfirmDialog msg = new LConfirmDialog(getWindow(), 
					vocab.UNSAVEDPROJECT,
					vocab.UNSAVEDMSG,
					LConfirmDialog.YES_NO_CANCEL);
			int result = msg.open();
			if (result == LConfirmDialog.YES) {
				if (!projectSerializer.save()) {
					LErrorDialog error = new LErrorDialog(getWindow(),
							vocab.SAVEERROR,
							vocab.SAVEERRORMSG);
					error.open();
					return false;
				} else {
					LActionManager.getInstance().onSave();
					return true;
				}
			} else if (result == LConfirmDialog.NO) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines()
				.forEach(consumer);
		}
	}	

}
