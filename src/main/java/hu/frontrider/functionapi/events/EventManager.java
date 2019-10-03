package hu.frontrider.functionapi.events;

import hu.frontrider.functionapi.ScriptedObject;
import hu.frontrider.functionapi.events.runners.EventRunner;
import hu.frontrider.functionapi.events.runners.EventRunnerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Handles an event
 */
public class EventManager {

    /**
     * The different runners allow us to hook into the event with different techniques.
     * Runners are build by factories, that are loaded as java services.
     */
    private static final LinkedList<EventRunnerFactory> eventRunnerFactories = new LinkedList<>();

    static {
        ServiceLoader<EventRunnerFactory> runnerFactories = ServiceLoader.load(EventRunnerFactory.class);
        runnerFactories.iterator().forEachRemaining(eventRunnerFactory -> {
            if (eventRunnerFactory.enabled())
                eventRunnerFactories.add(eventRunnerFactory);
        });
    }

    //the object that the event targets
    private final ScriptedObject target;
    /*the id of the event*/
    private final Identifier identifier;
    /**
     * If the handler is a singleton,
     * */
    private final boolean isSingleton;

    private boolean enabled = true;

    private List<EventRunner> eventRunners;

    public EventManager(ScriptedObject target, String eventName,boolean isSingleton) {
        this(target, new Identifier(target.getID().getNamespace(), target.getType() + "/" + target.getID().getPath() + "/" + eventName),isSingleton);
    }
    public EventManager(ScriptedObject target, String eventName) {
        this(target, new Identifier(target.getID().getNamespace(), target.getType() + "/" + target.getID().getPath() + "/" + eventName),false);
    }
    public EventManager(ScriptedObject target, Identifier identifier) {
        this(target,identifier,false);
    }
    public EventManager(ScriptedObject target, Identifier identifier,boolean isSingleton) {
        this.target = target;
        this.identifier = identifier;
        this.isSingleton = isSingleton;
        GlobalEventContainer.getInstance().addManager(this);

    }

    public void serverInit(MinecraftServer server) {
        GlobalEventContainer.getInstance().initCallback(identifier, server);
    }


    /**
     * call it with the correct context when the event should run.
     */
    public void fire(ServerCommandSource commandContext) {
        if (enabled) {
            GlobalEventContainer.getInstance().addIfMissing(this);

            if(isSingleton) {
                commandContext.getMinecraftServer().send(new ServerTask(1, () -> {
                    getRunners().get(0).fire(commandContext);
                }));
            }else {
                for (EventRunner eventRunner : getRunners()) {
                    commandContext.getMinecraftServer().send(new ServerTask(1, () -> {
                        eventRunner.fire(commandContext);
                    }));
                }
            }
        }
    }

    public void markDirty() {
        for (EventRunner eventRunner : getRunners()) {
            eventRunner.markDirty();
        }
    }

    /**
     * Returns a list of the available runners.
     * this is done at the first time the event has run, it allows for enough dynamism to make it work with functions.
     */
    private List<EventRunner> getRunners() {
        if (eventRunners == null) {
            eventRunners = new LinkedList<>();
            eventRunnerFactories
                    .iterator()
                    .forEachRemaining(eventRunnerFactory -> {
                        eventRunners.add(eventRunnerFactory.newEvent(identifier));
                    });
        }
        return eventRunners;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean hasEvents() {
        //if it's disabled, then we say that we have no events.
        if (!enabled) {
            return false;
        }
        //safety check.
        if (eventRunners != null) {
            //if any runner has events, return true.
            for (EventRunner eventRunner : eventRunners) {
                if (eventRunner.hasEvents())
                    return true;
            }
        }
        //if we have no runners with events, return false.
        return false;
    }

    public boolean isSingleton(){
            return isSingleton;
    }

    public Identifier getID() {
        return identifier;
    }

    public boolean hasEvents(MinecraftServer server) {
        eventRunners.forEach(eventRunner -> eventRunner.reload(server));
        return hasEvents();
    }
}
