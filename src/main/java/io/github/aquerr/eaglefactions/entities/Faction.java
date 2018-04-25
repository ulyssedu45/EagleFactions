package io.github.aquerr.eaglefactions.entities;

import io.github.aquerr.eaglefactions.managers.FlagManager;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Aquerr on 2017-07-13.
 */
public class Faction
{
    public String Name;
    public String Tag;
    public BigDecimal Power;
    public List<String> Members;
    public List<String> Alliances;
    public List<String> Enemies;
    public String Leader;
    public List<String> Officers;
    public List<String> Claims;
    public FactionHome Home;
    public Map<FactionMemberType, Map<FactionFlagType, Boolean>> Flags;

    //Constructor used while creating a new faction.
    public Faction(String factionName, String factionTag, String factionLeader)
    {
        this.Name = factionName;
        this.Tag = factionTag;
        this.Leader = factionLeader;
        this.Power = new BigDecimal("0.0");
        this.Members = new ArrayList<>();
        this.Claims = new ArrayList<>();
        this.Officers = new ArrayList<>();
        this.Alliances = new ArrayList<>();
        this.Enemies = new ArrayList<>();
        this.Home = null;
        this.Flags = FlagManager.getDefaultFactionFlags();
    }

    //Constructor used while getting a faction from storage.
    public Faction(String factionName, String factionTag, String factionLeader, List<String> members, List<String> claims, List<String> officers, List<String> alliances, List<String> enemies, FactionHome home, Map<FactionMemberType, Map<FactionFlagType, Boolean>> flags)
    {
        this.Name = factionName;
        this.Tag = factionTag;
        this.Leader = factionLeader;
        this.Power = new BigDecimal("0.0");
        this.Members = members;
        this.Claims = claims;
        this.Officers = officers;
        this.Alliances = alliances;
        this.Enemies = enemies;
        this.Home = home;
        this.Flags = flags;
    }
}
