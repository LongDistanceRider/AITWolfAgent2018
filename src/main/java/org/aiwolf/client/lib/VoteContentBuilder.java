/**
 * VoteContentBuilder.java
 * 
 * Copyright (c) 2016 人狼知能プロジェクト
 */
package org.aiwolf.client.lib;

import org.aiwolf.common.data.Agent;

/**
 * <div lang="ja">投票発話ビルダークラス</div>
 * 
 * <div lang="en">Builder class for the utterance of a vote.</div>
 * 
 * @author otsuki
 *
 */
public class VoteContentBuilder extends ContentBuilder {

	/**
	 * <div lang="ja">追放投票意思表明のためのVoteContentBuilderを構築する</div>
	 *
	 * <div lang="en">Constructs a VoteContentBuilder to declare the vote of execution.</div>
	 * 
	 * @param target
	 *            <div lang="ja">被投票エージェント</div>
	 * 
	 *            <div lang="en">The agent to be voted for.</div>
	 * 
	 */
	public VoteContentBuilder(Agent target) {
		topic = Topic.VOTE;
		this.target = target;
	}

	@Override
	String getText() {
		return ContentBuilder.join(" ", new String[] { subject == null ? "" : subject.toString(), Topic.VOTE.toString(), target.toString() }).trim();
	}

}
