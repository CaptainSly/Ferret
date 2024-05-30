package io.azraein.ferret.system._javafx;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.fx.drift.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.Callback;

import io.azraein.ferret.TestScreen;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.Registry;
import io.azraein.ferret.system.calendar.Calendar;
import io.azraein.ferret.system.gfx.FerretScreen;
import io.azraein.ferret.system.input.Input;
import io.azraein.ferret.system.lua.FerretLua;
import io.azraein.ferret.system.utilities.Utils;
import javafx.scene.layout.BorderPane;

/**
 * I got stuck at the point of trying to implement GLFW callbacks, not realizing
 * that we don't have a GLFW Context. I've realized since, that I'm probably
 * going to get input by using JavaFX listeners and whatnot, Controller support
 * will be in the air though. FXGL has controller support through SDL, so maybe
 * finding a way to create a binding or something for Drift. Till then, this is
 * deprecated and unusable.
 * 
 * 
 * 
 * If you would like to test this, Edit FerretScreen to use FXEngine instead of
 * Engine, and fix any other screen classes so they do the same. Should just
 * work out of the box unless the screens get updated serverly.
 * 
 */

@Deprecated
@SuppressWarnings("unused")
public class FXEngine extends BorderPane {

	private int width = 1024;
	private int height = 768;

	private float targetUps = 30;
	private float targetFps = 60;

	private int fpsCounter = 0;

	private long ctxPointer;
	private DriftFXSurface surface;
	private TransferType curTxType;
	private GLCapabilities caps;
	private Callback debugProc;
	private Thread lwjglThread;

	private boolean isAlive;

	private Swapchain swapChain;
	private Renderer renderer;

	private TransferType txType = StandardTransferTypes.MainMemory;

	private FerretScreen ferretScreen;
	private FerretLua ferretLua;

	public FXEngine(int width, int height) {
		this.width = width;
		this.height = height;

		Ferret.FERRET_VERSION += " -JavaFX Backend";
		
		setPrefSize(width, height);

		surface = new DriftFXSurface();
		renderer = GLRenderer.getRenderer(surface);

		lwjglThread = new Thread(this::lwjglRun);
		lwjglThread.setDaemon(true);

		setCenter(surface);
	}

	private void lwjglInit() throws IOException {
		// THE HOLY TRINITY: PLACE ABSOLUTELY EVERYTHING OPENGL AFTER THESE 3 LINES
		// ELSE YOU'LL FUCK UP HORRIBLY
		ctxPointer = org.eclipse.fx.drift.internal.GL.createSharedCompatContext(0);
		org.eclipse.fx.drift.internal.GL.makeContextCurrent(ctxPointer);
		caps = GL.createCapabilities();

		// The fucking screen starts here.
//		this.ferretScreen = new TestScreen(this);

		// Initialize Global Ferret objects
		Ferret.gameCalendar = new Calendar();
		Ferret.registry = new Registry();
		Ferret.input = new Input();

		// Initialize and Load the Ferret LuaLib last,
		// but before ferretScreen init
		ferretLua = new FerretLua();

		// Initialize Current Screen
		ferretScreen.onInit();

		glClearColor(0f, 1f, 1f, 1f);
		glEnable(GL_DEPTH_TEST);
	}

	private void lwjglUpdate(float delta) {

		// Game Calendar
		lastCalendarUpdateTime += delta;
		if (lastCalendarUpdateTime >= 1.32f) {
			Ferret.gameCalendar.update();
			lastCalendarUpdateTime = 0;
		}

		ferretScreen.updateScreen(delta);
	}

	private void lwjglRender() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0f, 1f, 1f, 1f);

		glEnable(GL_DEPTH_TEST);
		ferretScreen.onRender();

		glDisable(GL_DEPTH_TEST);
	}

	// Game Calendar Stuff, needed to be close to engine loop as possible.
	private float lastCalendarUpdateTime = 0.0f;

	private void lwjglLoop() {
		long initialTime = System.currentTimeMillis();
		float timeUpdate = 1000.0f / targetUps;
		float timeRender = targetFps > 0 ? 1000.0f / targetFps : 0;

		float deltaUpdate = 0;
		float deltaFps = 0;

		long updateTime = initialTime;
		while (isAlive) {

			long now = System.currentTimeMillis();
			deltaUpdate += (now - initialTime) / timeUpdate;
			deltaFps += (now - initialTime) / timeRender;

			Vec2i size = renderer.getSize();
			if (swapChain == null || size.x != width || size.y != height || curTxType != txType) {
				if (swapChain != null) {
					swapChain.dispose();
				}
				try {
					swapChain = renderer
							.createSwapchain(new SwapchainConfig(size, 2, PresentationMode.MAILBOX, txType));

					width = size.x;
					height = size.y;

					curTxType = txType;

					// Resize Anything that needs it.
					ferretScreen.onResize(width, height);
				} catch (Exception e) {
					System.err.println("swapchain recreation failed! " + e.getMessage());
					e.printStackTrace(System.err);
				}

			}

			if (deltaUpdate >= 1) {
				float delta = (float) (now - updateTime) / 1000.0f;
				lwjglUpdate(delta);
				updateTime = now;
				deltaUpdate--;
			}

			if (targetFps <= 0 || deltaFps >= 1) {

				if (swapChain != null) {
					try {

						RenderTarget target = swapChain.acquire();

						int tex = GLRenderer.getGLTextureId(target);
						int depthTex = glGenTextures();
						glBindTexture(GL_TEXTURE_2D, depthTex);
						glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT,
								GL_FLOAT, (ByteBuffer) null);
						glBindTexture(GL_TEXTURE_2D, 0);

						int fb = glGenFramebuffers();

						glBindFramebuffer(GL_FRAMEBUFFER, fb);
						glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, tex, 0);
						glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTex, 0);

						int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
						switch (status) {
						case GL_FRAMEBUFFER_COMPLETE:
							break;
						case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
							System.err.println("INCOMPLETE_ATTACHMENT!");
							break;
						}

						glViewport(0, 0, width, height);
						lwjglRender();

						glBindFramebuffer(GL_FRAMEBUFFER, 0);
						glDeleteFramebuffers(fb);
						glDeleteTextures(depthTex);

						swapChain.present(target);

					} catch (Exception e) {
						e.printStackTrace();
					}

					deltaFps--;
				}
			}

			initialTime = now;

		}

		if (swapChain != null) {
			swapChain.dispose();
			swapChain = null;
		}
	}

	private void lwjglRun() {
		if (isAlive)
			return;

		isAlive = true;
		try {
			lwjglInit();
			lwjglLoop();
			lwjglDispose();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void lwjglDispose() {
		if (debugProc != null)
			debugProc.free();
	}

	public int getFpsCounter() {
		return fpsCounter;
	}

	public long getCtxPointer() {
		return ctxPointer;
	}

	public DriftFXSurface getSurface() {
		return surface;
	}

	public TransferType getCurTxType() {
		return curTxType;
	}

	public GLCapabilities getCaps() {
		return caps;
	}

	public Callback getDebugProc() {
		return debugProc;
	}

	public Thread getLwjglThread() {
		return lwjglThread;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public Swapchain getSwapChain() {
		return swapChain;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public TransferType getTxType() {
		return txType;
	}

	public FerretLua getLua() {
		return ferretLua;
	}

}
