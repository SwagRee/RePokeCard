package io.github.swagree.pokecard.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举类，枚举所有卡的名称和对应的中文名
 * 方便后续调用显示
 */
public enum  EnumCardName {

    Mt("mt", "梦特"),
    Shiny("shiny", "闪光"),
    UnShiny("unShiny", "解闪光"),
    MaxLevel("maxLevel", "满级"),
    ClearLevel("clearLevel", "等级清空"),
    Gender("gender", "性别"),
    ClearEvs("clearEvs", "努力值清空"),
    MaxIvs("maxIvs", "满V"),
    AnyIvs("anyIvs", "任意V"),
    AnyZeroIvs("anyZeroIvs", "任意0V"),
    AnyEvs("anyEvs", "任意努力值"),
    rdGrowth("rdGrowth", "随机体型"),
    AnyGrowth("anyGrowth", "任意体型"),
    AnyNature("anyNature", "任意性格"),
    rdNature("rdNature", "随机性格"),
    Form("form", "形态"),
    RdForm("rdForm", "随机形态"),
    FormLegendary("formLegendary", "神兽形态"),
    FormCommon("formCommon", "普通宝可梦形态"),
    FromDetail("formDetail", "指定宝可梦形态"),
    Move("move", "技能"),
    PokeBall("pokeBall", "改球种"),
    Bind("bind", "绑定"),
    UnBind("unbind", "解绑"),
    Breed("breed", "绝育"),
    UnBreed("unBreed", "解绝育"),
    Hatch("hatch", "秒孵");

    private final String cardName;
    private final String cardNameCN;

    private static final Map<String, String> nameToCNMap = new HashMap<>();

    static {
        for (EnumCardName enumCard : EnumCardName.values()) {
            nameToCNMap.put(enumCard.getCardName(), enumCard.getCardNameCN());
        }
    }

    EnumCardName(String cardName, String cardNameCN) {
        this.cardName = cardName;
        this.cardNameCN = cardNameCN;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardNameCN() {
        return cardNameCN;
    }

    public static String getValueByKey(String key) {
        return nameToCNMap.get(key);
    }
}