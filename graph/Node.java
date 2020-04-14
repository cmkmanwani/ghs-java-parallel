package graph;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import util.ChannelStatus;
import util.MType;
import util.Message;
import util.NodeState;

public class Node extends Thread {
    private NodeState state;
    private int fID;
    private int level;
    private int bestWeight;

    private ArrayList<Channel> channels;

    private Channel testCh;
    private Channel bestCh;
    private Channel parent;

    private int recP;
    private LinkedBlockingQueue<Message> q;


    /*
        Node Constructor Functions
    */

    public Node() {
        state = NodeState.NULL;
        fID = -1;
        level = -1;
        bestWeight = -1;
        channels = null;
        testCh = null;
        bestCh = null;
        parent = null;
        recP = -1;
        q = null;
    }

    public Node(int fID) {
        this.fID = fID;
        state = NodeState.SLEEP;
        level = 0;
        bestWeight = -1;
        channels = new ArrayList<>();
        testCh = null;
        bestCh = null;
        parent = null;
        recP = 0;
        q = new LinkedBlockingQueue<>();
    }

    /*
        Helper Functions
    */

    public void addChannel(Channel c) {
        if (c != null) {
            channels.add(c);
        }
    }

    public void addMessage(Message m) {
        try {
            q.put(m);
        } catch (InterruptedException e) {
            System.out.println("Node interrupted while adding message");
        }
    }

    private Channel getBestChannel() {
        Channel bestChannel = null;
        int bestWeight = Integer.MAX_VALUE;

        for(Channel c : channels) {
            int cWeight = c.getWeight();

            if(c.getStatus() == ChannelStatus.BASIC && cWeight < bestWeight) {
                bestWeight = cWeight;
                bestChannel = c;
            }
        }

        return bestChannel;
    }

    private Channel getSenderChannel(Node sender) {
        for (Channel c : channels) {
            if(sender == c.getNode()) {
                return c;
            }
        }
        return null;
    }

    private void processMessage(Message m) {
        MType mType = m.getType();
        switch(mType){
            case WAKEUP:
                wakeUp();
                break;
            case CONNECT:
                connect(m);
                break;
            case INITITATE:
                System.out.println("Initiate Message Received");
                break;
            case TEST:
                System.out.println("Test Message Received");
                break;
            case ACCEPT:
                System.out.println("Accept Message Received");
                break;
            case REJECT:
                System.out.println("Reject Message Received");
                break;
            case REPORT:
                System.out.println("Report Message Received");
                break;
            case CHANGEROOT:
                System.out.println("Changeroot Message Received");
                break;
            default:
                System.out.println("Improper message code");
        }

    }

    /*
        Thread run function
    */
    @Override
    public void run() {
        while (true) {
            try {
                Message m = q.take();
                processMessage(m);
            } catch (InterruptedException e) {
                System.out.println();
            }
        }
    }

    /*
        GHS Functions
    */
    private void wakeUp() {
        Channel bestChannel = getBestChannel();
        bestChannel.setStatus(ChannelStatus.BRANCH);
        this.level = 0;
        this.state = NodeState.FOUND;
        this.recP = 0;

        Message m = new Message(MType.CONNECT, this.level, this);
        Node recipient = bestChannel.getNode();
        recipient.addMessage(m);
    }

    private void connect(Message m) {
        if (this.state == NodeState.SLEEP) {
            wakeUp();
        }

        int L = m.getLevel();
        Node sender = m.getSender();
        Channel sChannel = getSenderChannel(sender);
        if (L < this.level) {
            sChannel.setStatus(ChannelStatus.BRANCH);
            Message initM = new Message(MType.INITITATE, this.level, this.fID, this.state, this);
            sender.addMessage(initM);
        }
        else if (sChannel.getStatus() == ChannelStatus.BASIC) {
            addMessage(m);
        }
        else {
            Message initM = new Message(MType.INITITATE, this.level + 1, sChannel.getWeight(), NodeState.FIND, this);
            sender.addMessage(initM);
        }
    }

    private void initiate(Message m) {

    }

    private void test(Message m) {

    }

    private void test() {

    }

    private void accept(Message m) {

    }

    private void reject(Message m) {

    }

    private void report(Message m) {

    }

    private void report() {

    }

    private void changeroot(Message m) {

    }

    private void changeroot() {

    }
}