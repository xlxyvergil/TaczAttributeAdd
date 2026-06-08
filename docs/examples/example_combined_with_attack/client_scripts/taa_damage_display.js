// TAA 伤害显示 - 客户端脚本
// 在面板渲染时重新计算伤害显示值，与服务端KJS加成保持一致
TAAPropertyDisplayEvents.propertyDisplay(event => {
    var propertyType = event.propertyType;
    if (propertyType !== 'DAMAGE') return;

    var player = event.player;
    var currentDisplay = event.displayValue;
    var originalValue = event.originalValue;

    // 获取 L2DamageTracker 弓强度乘数
    var bowStrengthMultiplier = 1.0;
    var bowStrength = player.getAttribute('l2damagetracker:bow_strength');
    if (bowStrength) {
        bowStrengthMultiplier = bowStrength.value;
    }

    // 获取 Apothic-Attributes 弹射物伤害乘数
    var arrowDamageMultiplier = 1.0;
    var arrowDamage = player.getAttribute('attributeslib:arrow_damage');
    if (arrowDamage) {
        arrowDamageMultiplier = arrowDamage.value;
    }

    // 获取攻击伤害加成
    var attackDamageBonus = 0;
    var attackDamage = player.getAttribute('minecraft:generic.attack_damage');
    if (attackDamage) {
        attackDamageBonus = attackDamage.value;
    }

    // 公式: (当前显示值 - 攻击伤害加成) × 弓强度 × 弹射物伤害 + 攻击伤害加成
    // 当前displayValue = originalValue × TAA属性加成(不含KJS)
    // 我们只需在此基础上乘古遗物和神化的加成，攻击伤害是加法已处理
    var newDisplay = currentDisplay * bowStrengthMultiplier * arrowDamageMultiplier + attackDamageBonus;

    event.setDisplayValue(newDisplay);
    console.log(`TAA Display: DAMAGE ${currentDisplay.toFixed(2)} → ${newDisplay.toFixed(2)} (bow:${bowStrengthMultiplier.toFixed(2)}, arrow:${arrowDamageMultiplier.toFixed(2)}, atk:${attackDamageBonus.toFixed(2)})`);
});
