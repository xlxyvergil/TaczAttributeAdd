// TAA 面板伤害显示 - 古遗物 + 神化
TAAPropertyDisplayEvents.propertyDisplay(event => {
    if (event.propertyType !== 'DAMAGE') return;

    var player = event.player;

    var bowMult = 1.0;
    var bowAttr = player.getAttribute('l2damagetracker:bow_strength');
    if (bowAttr) bowMult = bowAttr.value;

    var arrowMult = 1.0;
    var arrowAttr = player.getAttribute('attributeslib:arrow_damage');
    if (arrowAttr) arrowMult = arrowAttr.value;

    event.setDisplayValue(event.displayValue * bowMult * arrowMult);
});
