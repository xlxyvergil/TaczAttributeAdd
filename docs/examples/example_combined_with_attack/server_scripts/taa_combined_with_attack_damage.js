// server_scripts/taa_combined_with_attack_damage.js
// 示例4：两个加成相乘后再附加攻击伤害实体属性加成（加法）

// 获取 DistanceDamagePair 类（KubeJS 2001 使用 Java.loadClass）
var DistanceDamagePair = Java.loadClass('com.tacz.guns.resource.pojo.data.gun.ExtraDamage$DistanceDamagePair');
// 获取 LinkedList 类，用于创建 damage List
var LinkedList = Java.loadClass('java.util.LinkedList');

TAAContextEvents.attributePost(event => {
    var shooter = event.shooter;
    var results = event.results;

    // ========== 获取 L2DamageTracker 弓强度乘数 ==========
    // 注意：L2Artifacts 的 bow_strength_add 统计类型实际引用的是 l2damagetracker:bow_strength 属性
    var bowStrengthMultiplier = 1.0;
    var bowStrength = shooter.getAttribute('l2damagetracker:bow_strength');
    if (bowStrength) {
        bowStrengthMultiplier = bowStrength.value;
    }

    // ========== 获取 Apothic-Attributes 弹射物伤害乘数 ==========
    var arrowDamageMultiplier = 1.0;
    var arrowDamage = shooter.getAttribute('attributeslib:arrow_damage');
    if (arrowDamage) {
        arrowDamageMultiplier = arrowDamage.value;
    }

    // ========== 获取攻击伤害实体属性加成（加法） ==========
    // 这里以 minecraft:generic.attack_damage 为例，你可以替换为其他攻击伤害属性
    var attackDamageBonus = 0;
    var attackDamage = shooter.getAttribute('minecraft:generic.attack_damage');
    if (attackDamage) {
        attackDamageBonus = attackDamage.value;
    }

    var damageList = results.getDamage();
    if (damageList && damageList.size() > 0) {
        // 输出实体名称
        var entityName = shooter.getName().getString();
        console.log(`TAA: Entity name: ${entityName}`);
        
        // 使用 Java LinkedList 而不是 JS 数组
        var newDamageList = new LinkedList();
        var iterator = damageList.iterator();
        while (iterator.hasNext()) {
            var pair = iterator.next();
            var distance = pair.getDistance();
            var currentDamage = pair.getDamage();

            // 公式: 原伤害 × 古遗物 × 神化 + 攻击伤害加成
            // 不需要判断是否大于1，乘以1还是原值
            var newDamage = currentDamage * bowStrengthMultiplier * arrowDamageMultiplier + attackDamageBonus;

            // 输出修改前后的伤害值
            console.log(`TAA: Distance ${distance}: before=${currentDamage.toFixed(2)}, after=${newDamage.toFixed(2)}`);

            // 使用 new 关键字直接创建 DistanceDamagePair 实例
            newDamageList.add(new DistanceDamagePair(distance, newDamage));
        }

        results.setDamage(newDamageList);
        console.log(`TAA: Combined - bow_strength: ${bowStrengthMultiplier}, arrow_damage: ${arrowDamageMultiplier}, attack_damage: ${attackDamageBonus}`);
        
        // 验证修改是否生效
        var modifiedDamageList = results.getDamage();
        if (modifiedDamageList && modifiedDamageList.size() > 0) {
            var modifiedIterator = modifiedDamageList.iterator();
            console.log("TAA: Modified damage list verification:");
            while (modifiedIterator.hasNext()) {
                var modifiedPair = modifiedIterator.next();
                console.log(`TAA:   Distance ${modifiedPair.getDistance()}: ${modifiedPair.getDamage().toFixed(2)}`);
            }
        }
    } else {
        console.log("TAA: damageList is null or empty!");
    }
});