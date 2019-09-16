package hu.frontrider.functionapi;

import net.minecraft.util.Identifier;

/**
 *  Contains information that is required for scripting.
 * */
public interface ScriptedObject {

    /**
     * The object is marked for a reload. All caches should be prepared for a reload/reloaded.
     * Called after every datapack load/reload.
     * */
    void markDirty();

    /**
     * Get the identifier of this object (eg: minecraft:dirt)
     * */
    Identifier getID();

    /**
     * Get the type of this object, eg "block".
     * */
    String getType();
}
