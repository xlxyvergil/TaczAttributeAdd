// server_scripts/taa_l2artifacts_bow_strength.js
// 示例1：只使用古遗物中弹射物加成

var DistanceDamagePair = Java.loadClass('com.tacz.guns.resource.pojo.data.gun.ExtraDamage$DistanceDamagePair');
var LinkedList = Java.loadClass('java.util.LinkedList');

TAAContextEvents.attributePost(event => {
    var shooter = event.shooter;
    var results = event.results;

    var bowMult = 1.0;
    var bowAttr = shooter.getAttribute('l2damagetracker:bow_strength');
    if (bowAttr) bowMult = bowAttr.value;

    var damageList = results.getDamage();
    if (!damageList || damageList.size() === 0) return;

    var newList = new LinkedList();
    var it = damageList.iterator();
    while (it.hasNext()) {
        var pair = it.next();
        var newDamage = pair.getDamage() * bowMult;
        newList.add(new DistanceDamagePair(pair.getDistance(), newDamage));
    }
    results.setDamage(newList);
    console.log(`TAA: L2Artifacts bow_strength=${bowMult.toFixed(2)}`);
});
