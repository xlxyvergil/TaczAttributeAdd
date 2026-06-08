// TAA 面板伤害显示 - 神化加成
TAAPropertyDisplayEvents.propertyDisplay(event => {
    if (event.propertyType !== 'DAMAGE') return;

    var player = event.player;

    var arrowMult = 1.0;
    var arrowAttr = player.getAttribute('attributeslib:arrow_damage');
    if (arrowAttr) arrowMult = arrowAttr.value;

    event.setDisplayValue(event.displayValue * arrowMult);
});
