package de.tr7zw.gw2racing;

import java.io.File;

import javax.swing.UIManager;

import de.tr7zw.gw2racing.gui.PaintGui;
import de.tr7zw.gw2racing.gui.OverlayInterface;
import de.tr7zw.gw2racing.gui.system.GuiOverlay;
import de.tr7zw.gw2racing.tracks.Race;
import de.tr7zw.gw2racing.tracks.TestTrackGenerator;
import de.tr7zw.gw2racing.util.APIHelper;
import de.tr7zw.gw2racing.util.MumbleLink;
import de.tr7zw.gw2racing.util.PresenceUpdater;
import de.tr7zw.gw2racing.util.ProcessChecker;
import de.tr7zw.gw2racing.util.RegionManager;
import lombok.Getter;
import lombok.Setter;

public class Gw2Racing {

    private static Gw2Racing INSTANCE;

    static final String PROCESS_NAME = "Gw2-64.exe";

    @Getter
    File dataDir;
    @Getter
    MumbleLink mumbleLink;
    @Getter
    PresenceUpdater presenceUpdater;
    @Getter
    APIHelper apiHelper;
    @Getter
    RegionManager regionManager;
    PaintGui dialog;
    @Getter
    boolean processRunning = false;
    public boolean shouldShutdown = false;
    private Gw2DataProvider dataProvider = new Gw2DataProvider();

    @Getter
    @Setter
    private String loadingState = "Waiting...";

    @Getter
    @Setter
    private Race race = new Race(TestTrackGenerator.getLATrack());

    public Gw2Racing() throws InterruptedException {
	INSTANCE = this;
	this.dataDir = new File("data");
	if (!this.dataDir.exists()) {
	    this.dataDir.mkdir();
	}

	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception ignored) {
	}

	this.dialog = new PaintGui(this);
	dialog.initComponents();

	new Thread(() -> {
	    ProcessChecker processChecker = new ProcessChecker();
	    while (true) {
		boolean check = processChecker.isProcessRunning(PROCESS_NAME);
		if (!processRunning) {
		    setLoadingState("Waiting for Guild Wars 2");
		}
		if (!processRunning && check) {
		    System.out.println("Guild Wars 2 started!");
		    setLoadingState("Guild Wars 2 started! Waiting for info...");
		    processRunning = true;

		    startup();
		}
		if (processRunning && !check || shouldShutdown) {
		    processRunning = false;
		    shutdown();
		    break;
		}

		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}).start();
	this.dialog.setVisible(true);
    }

    private void startup() {
	this.presenceUpdater = new PresenceUpdater(this);
	this.apiHelper = new APIHelper(this);
	regionManager = new RegionManager(this);

	System.out.println("Starting MumbleLink thread...");
	setLoadingState("Starting MumbleLink thread...");
	this.mumbleLink = new MumbleLink();
	this.mumbleLink.start();
	onReady();
    }

    public void onReady() {
	this.presenceUpdater.start();
	setLoadingState("Waiting for Character selection...");
    }

    public void doneLoading() {
	getGui().openScreen(new GuiOverlay());
    }

    private void shutdown() {
	System.out.println("Shutting down...");

	if (this.mumbleLink != null) {
	    this.mumbleLink.kill();
	}
	if (this.presenceUpdater != null) {
	    this.presenceUpdater.kill();
	}

	System.exit(0);
    }

    public static Gw2Racing getInstance() {
	return INSTANCE;
    }

    public OverlayInterface getGui() {
	return dialog;
    }

    public Gw2DataProvider getDataProvider() {
	return dataProvider;
    }

}
