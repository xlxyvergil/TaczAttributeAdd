// TAA 面板伤害显示 - 古遗物加成
TAAPropertyDisplayEvents.propertyDisplay(event => {
    if (event.propertyType !== 'DAMAGE') return;

    var player = event.player;

    var bowMult = 1.0;
    var bowAttr = player.getAttribute('l2damagetracker:bow_strength');
    if (bowAttr) bowMult = bowAttr.value;

    event.setDisplayValue(event.displayValue * bowMult);
});
