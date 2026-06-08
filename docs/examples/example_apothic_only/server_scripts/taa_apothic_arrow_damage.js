// server_scripts/taa_apothic_arrow_damage.js
// 示例2：只使用神化属性中弹射物加成

var DistanceDamagePair = Java.loadClass('com.tacz.guns.resource.pojo.data.gun.ExtraDamage$DistanceDamagePair');
var LinkedList = Java.loadClass('java.util.LinkedList');

TAAContextEvents.attributePost(event => {
    var shooter = event.shooter;
    var results = event.results;

    var arrowMult = 1.0;
    var arrowAttr = shooter.getAttribute('attributeslib:arrow_damage');
    if (arrowAttr) arrowMult = arrowAttr.value;

    var damageList = results.getDamage();
    if (!damageList || damageList.size() === 0) return;

    var newList = new LinkedList();
    var it = damageList.iterator();
    while (it.hasNext()) {
        var pair = it.next();
        var newDamage = pair.getDamage() * arrowMult;
        newList.add(new DistanceDamagePair(pair.getDistance(), newDamage));
    }
    results.setDamage(newList);
    console.log(`TAA: Apothic-Attributes arrow_damage=${arrowMult.toFixed(2)}`);
});
