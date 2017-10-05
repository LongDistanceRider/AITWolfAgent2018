/**
 * SampleRoleAssignPlayer.java
 * 
 * Copyright (c) 2016 人狼知能プロジェクト
 */
package org.aiwolf.sample.player;

import org.aiwolf.sample.lib.AbstractRoleAssignPlayer;

/**
 * Sampleのみを使って起動するPlayer
 * @author tori
 *
 */
public class SampleRoleAssignPlayer extends AbstractRoleAssignPlayer {

	public SampleRoleAssignPlayer(){
		setVillagerPlayer(new SampleVillager());
		setSeerPlayer(new SampleSeer());
		setMediumPlayer(new SampleMedium());
		setBodyguardPlayer(new SampleBodyguard());
		setPossessedPlayer(new SamplePossessed());
		setWerewolfPlayer(new SampleWerewolf());
	}

	@Override
	public String getName() {
		return SampleRoleAssignPlayer.class.getSimpleName();
	}	
}
