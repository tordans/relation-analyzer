package org.osmsurround.ra.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osmsurround.ra.segment.ISegment;

/**
 * A segment that contains only segments that can be connected.
 * 
 */
public class AggregatedSegment implements ISegment {

	private List<ISegment> segments = new ArrayList<ISegment>();

	public AggregatedSegment(ISegment segment) {
		addSegment(segment);
	}

	@Override
	public Collection<ConnectableNode> getStartNodes() {
		Collection<ConnectableNode> nodes = new ArrayList<ConnectableNode>();
		for (ISegment segment : segments)
			nodes.addAll(segment.getStartNodes());
		return nodes;
	}

	@Override
	public Collection<ConnectableNode> getEndNodes() {
		Collection<ConnectableNode> nodes = new ArrayList<ConnectableNode>();
		for (ISegment segment : segments)
			nodes.addAll(segment.getEndNodes());
		return nodes;
	}

	public void addSegment(ISegment segment) {
		if (segment instanceof AggregatedSegment) {
			addAllSegments(((AggregatedSegment)segment).getSegments());
		}
		else
			segments.add(segment);
	}

	public void addAllSegments(Collection<ISegment> segments) {
		this.segments.addAll(segments);
	}

	public List<ISegment> getSegments() {
		return segments;
	}
}
