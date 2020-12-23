package carsteering.controllers;

import carsteering.engine.Car;
import carsteering.engine.Game;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

/**
 *
 * @author santi
 * @author Greg Barasch
 */
public class KeyboardController extends Controller {

    public static final int key_accelerate = KeyEvent.VK_UP;
    public static final int key_brake = KeyEvent.VK_DOWN;
    public static final int key_left = KeyEvent.VK_LEFT;
    public static final int key_right = KeyEvent.VK_RIGHT;

    // store which keys are currently pressed:
    boolean[] keyboardState = new boolean[KeyEvent.KEY_LAST];

    public KeyboardController() {
        for(int i = 0;i<KeyEvent.KEY_LAST;i++) keyboardState[i] = false;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
                dispatcher -> {
                    synchronized (KeyboardController.class) {
                        switch (dispatcher.getID()) {
                            case KeyEvent.KEY_PRESSED -> keyboardState[dispatcher.getKeyCode()] = true;
                            case KeyEvent.KEY_RELEASED -> keyboardState[dispatcher.getKeyCode()] = false;
                        }
                        return false;
                    }
                });
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;

        if (keyboardState[key_left] && !keyboardState[key_right]) controlVariables[VARIABLE_STEERING] = -1;
        if (keyboardState[key_right] && !keyboardState[key_left]) controlVariables[VARIABLE_STEERING] = +1;
        if (keyboardState[key_accelerate]) controlVariables[VARIABLE_THROTTLE] = 1;
        if (keyboardState[key_brake]) controlVariables[VARIABLE_BRAKE] = 1;
    }

}
