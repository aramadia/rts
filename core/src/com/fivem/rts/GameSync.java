package com.fivem.rts;

import com.badlogic.gdx.Gdx;
import com.fivem.rts.command.AckCommand;
import com.fivem.rts.command.BaseCommand;
import com.fivem.rts.command.Command;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * How synchronization works
 *
 * == Game is in local state (not connected to a room) ==
 * - We still use gameSync so the code gets used
 * - However only requires one ack, and we send acks to ourselves
 * - Commands still get executed FRAME_DELAY later locally
 *
 * == Room Connected
 * - Detected by GoogleServiceInterface connected()
 * - However the network is still cold (for some reason it takes a few more seconds before you can send commands)
 * - Send the START command to all parties
 * - TODO: elect leader and pick random seed, game type etc.
 *
 * == Wait until NUM_PLAYERS Start is sent
 * - Official game start
 * - Reset gameSync curFrame counter
 * - Reset world so everyone has the same f0 world (reset random too)
 *
 * == Normal Game operation
 * - Commands get queued by GameSync, when the required acks for curFrame is accumulated, the commands are forwarded.
 * - All commands are sent with curFrame+FRAME_DELAY
 * - At the end of every curFrame, an ack for curFrame+FRAME_DELAY is sent
 *
 * Sync Frames,
 * syncFrame 0, maps to curFrame 0 - 4
 */
public class GameSync {
  private final static String TAG = GameSync.class.getSimpleName();

  //
  private int curFrame;

  private final static int FRAME_DELAY = 15;

  // All commands get snapped to the next sync interval.
  // This way acks are sent every 5th curFrame.
  private final int SYNC_INTERVAL = 15;

  private int acksNeeded = 1;

  // Commands buffered after start is called
  private ArrayList<Command> cachedCommands = new ArrayList<Command>();

  // Maps currentFrames to command buffers
  private HashMap<Integer, CommandBuffer> commandBuffer = new HashMap<Integer, CommandBuffer>();

  private int logOnceFrame = 0;



  class CommandBuffer {
    // Frame these commands should execute
    int frame;

    ArrayList<Command> commands = new ArrayList<Command>();

    // Number of clients acknowledged for this command
    int acknowledged = 0;
  }

  public void reset(int acksNeeded) {
    this.acksNeeded = acksNeeded;
    commandBuffer.clear();
    curFrame = 0;
    Gdx.app.log(TAG, "reset GameSync");
  }

  public int getCurFrame() {
    return curFrame;
  }

  public void setHash(int hashCode) {
    //  THe current curFrame has this hashCode
    Gdx.app.debug(TAG, "Frame: " + curFrame + " Hash: " + hashCode);
  }

  /**
   * Create an ack packet to send to indicate this curFrame is over.
   * However, this shoudln't be called if the startFrame is blocking
   * @return Command to send, or null if there is no command
   */
  public Command finishFrame() {


    if (curFrame % SYNC_INTERVAL != 0) {
      curFrame++;
      return null;
    }

    AckCommand ack = new AckCommand();
    ack.frameReady = getSyncFrame(curFrame);
    curFrame++;

    return Command.ackCommand(ack);
  }

  private CommandBuffer getOrCreateCommandBuffer(int frame) {
    if (frame % SYNC_INTERVAL != 0) {
      Gdx.app.error(TAG, "getOrCreate unsynced frame " + frame);
    }

    CommandBuffer buf = commandBuffer.get(frame);

    if (buf == null) {
      buf = new CommandBuffer();
      commandBuffer.put(frame, buf);
    }

    return buf;
  }

  /**
   * GameSync incoming commands from the network, return commands that should
   * be executing on this curFrame.
   * On an unsync frame, we buffer commands but don't do anything special with them.
   * @param incoming
   * @return A list of commands to execute, or null if the game should freeze
   */
  public ArrayList<Command> startFrame(ArrayList<Command> incoming) {

    // Prepend incoming with cachedCommands
    // TODO don't ask, but it works
    cachedCommands.addAll(incoming);
    incoming = cachedCommands;
    cachedCommands = new ArrayList<Command>();

    ArrayList<Command> outCommands = new ArrayList<Command>();

    for (int i = 0; i < incoming.size(); i++) {
      Command command = incoming.get(i);
      BaseCommand cmd = command.getCommand();

      // Handle start specially
      if (command.type == Command.Type.START) {
        outCommands.add(command);
        for (int j = i + 1; j < incoming.size(); j++) {
          cachedCommands.add(incoming.get(j));
        }
        return outCommands;
      }

      // Handle ack types here, do not forward them out
      if (command.type == Command.Type.ACK) {
        AckCommand ack = (AckCommand)cmd;
        CommandBuffer buf = getOrCreateCommandBuffer(ack.frameReady);
        buf.acknowledged++;
        continue;
      }

      // TODO Forward unsynchornized commands immediately?
      CommandBuffer buf = getOrCreateCommandBuffer(cmd.syncTime);
      buf.commands.add(command);

    }

    // Priming the sync manager, all frames < FRAME_DELAY should be free
    if (curFrame < FRAME_DELAY) {
      return new ArrayList<Command>();
    }

    if (curFrame % SYNC_INTERVAL != 0) {
      return new ArrayList<Command>();
    }

    // Get current curFrame
    CommandBuffer buf = commandBuffer.get(curFrame);

    // No commands to execute thats ok, no acks were sent yet
    if (buf == null) {
      if (curFrame != logOnceFrame) {
        Gdx.app.log(TAG, "No CommandBuffer for curFrame " + curFrame);
        logOnceFrame = curFrame;
      }
      return null;
    }

    // If everyone acknowledge this curFrame, execute these commands
    // TODO figure out why we are getting multiple acks for the same curFrame.  (Messaging is unreliable)
    if (buf.acknowledged >= acksNeeded) {
      outCommands.addAll(buf.commands);
      commandBuffer.remove(curFrame);
      return outCommands;
    }

    if (curFrame != logOnceFrame) {
      Gdx.app.log(TAG, "CommandBuffer " + curFrame + " has " + buf.acknowledged + " acks");
      logOnceFrame = curFrame;
    }
    return null;
  }

  /**
   * The command will run at (frame + FRAME_DELAY) rounded up the nearest SYNC_INTERVAL
   * @param frame
   * @return
   */
  public int getSyncFrame(int frame) {
    return (frame + FRAME_DELAY + SYNC_INTERVAL - 1)/ SYNC_INTERVAL * SYNC_INTERVAL;
  }

  /**
   * Synchronizes commands to run in the future
   * @param c
   */
  public Command synchronizeCommand(Command c) {
    // 3 rounds to 5, 5 == 5, 9 == 10
    int syncFrame =  getSyncFrame(curFrame);
    c.getCommand().syncTime = syncFrame;
    return c;
  }
}


