package re.imc.geysermodelengine.model;

import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import re.imc.geysermodelengine.GeyserModelEngine;
import re.imc.geysermodelengine.packet.entity.PacketEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ModelEntity {

    public static Map<Integer, Map<ActiveModel, ModelEntity>> ENTITIES = new ConcurrentHashMap<>();

    public static Map<Integer, ModelEntity> MODEL_ENTITIES = new ConcurrentHashMap<>();

    private PacketEntity entity;

    private final Set<Player> viewers = Sets.newConcurrentHashSet();

    private final ModeledEntity modeledEntity;

    private final ActiveModel activeModel;

    private EntityTask task;

    private ModelEntity(ModeledEntity modeledEntity, ActiveModel model) {
        this.modeledEntity = modeledEntity;
        this.activeModel = model;
        this.entity = spawnEntity();
        runEntityTask();
    }

    public void teleportToModel() {
        Location location = modeledEntity.getBase().getLocation();
        entity.teleport(location);
    }
    public static ModelEntity create(ModeledEntity entity, ActiveModel model) {
        ModelEntity modelEntity = new ModelEntity(entity, model);
        int id = entity.getBase().getEntityId();
        Map<ActiveModel, ModelEntity> map = ENTITIES.computeIfAbsent(id, k -> new HashMap<>());
        for (Map.Entry<ActiveModel, ModelEntity> entry : map.entrySet()) {
            if (entry.getKey() !=  model && entry.getKey().getBlueprint().getName().equals(model.getBlueprint().getName())) {
                return null;
            }
        }
        map.put(model, modelEntity);

        return modelEntity;
    }

    public PacketEntity spawnEntity() {
        entity = new PacketEntity(GeyserModelEngine.getInstance().getModelEntityType(), viewers, modeledEntity.getBase().getLocation());
        return entity;
    }

    public void runEntityTask() {
        task = new EntityTask(this);
        task.run(GeyserModelEngine.getInstance(), 0);
    }


    public void applyFeatures(LivingEntity display, String name) {
        display.setGravity(false);
        display.setMaxHealth(2048);
        display.setHealth(2048);
        display.setMetadata("model_entity", new FixedMetadataValue(GeyserModelEngine.getInstance(), true));

        //display.setInvulnerable(true);

        display.setAI(false);
        display.setSilent(true);
        display.setPersistent(false);

        // armorStand.setVisible(false);

        /*
        String uuid = UUID.randomUUID().toString();
        MobDisguise disguise = new MobDisguise(DisguiseType.getType(entity.getType()));
        disguise.setDisguiseName(uuid);

        DisguiseAPI.disguiseEntity(display, disguise);

         */

    }


}
