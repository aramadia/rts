package com.fivem.rts;

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
 * - Reset gameSync frame counter
 * - Reset world so everyone has the same f0 world (reset random too)
 *
 * == Normal Game operation
 * - Commands get queued by GameSync, when the required acks for curFrame is accumulated, the commands are forwarded.
 * - All commands are sent with curFrame+FRAME_DELAY
 * - At the end of every frame, an ack for curFrame+FRAME_DELAY is sent
 */
public class GameSync {
  private int frame;
  private final static int FRAME_DELAY = 12;
  private int acksNeeded = 1;

  private HashMap<Integer, CommandBuffer> commandBuffer = new HashMap<Integer, CommandBuffer>();

  class CommandBuffer {
    // Frame these commands should execute
    int frame;

    ArrayList<Command> commands = new ArrayList<Command>();

    // Number of clients acknowledged for this command
    int acknowledged;
  }

  public void reset(int acksNeeded) {
    this.acksNeeded = acksNeeded;
    commandBuffer.clear();
    frame = 0;
  }


  /**
   * Create an ack packet to send to indicate this frame is over
   */
  public Command finishFrame() {
    AckCommand ack = new AckCommand();
    ack.frameReady = frame + FRAME_DELAY;

    frame++;
    return Command.ackCommand(ack);
  }

  private CommandBuffer getOrCreateCommandBuffer(int frame) {
    CommandBuffer buf = commandBuffer.get(frame);

    if (buf == null) {
      buf = new CommandBuffer();
      commandBuffer.put(frame, buf);
    }

    return buf;
  }

  /**
   * GameSync incoming commands from the network, return commands that should
   * be executing on this frame
   * @param incoming
   * @return A list of commands to execute, or null if the game should freeze
   */
  public ArrayList<Command> startFrame(ArrayList<Command> incoming) {

    ArrayList<Command> outCommands = new ArrayList<Command>();

    for (Command command: incoming) {
      BaseCommand cmd = command.getCommand();

      // Handle ack types here
      if (command.type == Command.Type.ACK) {
        AckCommand ack = (AckCommand)cmd;
        CommandBuffer buf =getOrCreateCommandBuffer(ack.frameReady);
        buf.acknowledged++;
      }

      // Forward unsynchornized commands immediately
      CommandBuffer buf = getOrCreateCommandBuffer(cmd.syncTime);
      buf.commands.add(command);

    }

    // Priming the sync manager, all frames < FRAME_DELAY should be free
    if (frame < FRAME_DELAY) {
      return new ArrayList<Command>();
    }

    // Get current frame
    CommandBuffer buf = commandBuffer.get(frame);

    // No commands to execute thats ok, no acks were sent yet
    if (buf == null) {
      return null;
    }

    // If everyone acknowledge this frame, execute these commands
    if (buf.acknowledged == acksNeeded) {
      outCommands.addAll(buf.commands);
      commandBuffer.remove(frame);
      return outCommands;
    }

    return null;
  }

  /**
   * Synchronizes a command to run at a certain frame.
   * @param c
   */
  public Command synchronizeCommand(Command c) {
    c.getCommand().syncTime = frame + FRAME_DELAY;
    return c;
  }
}


