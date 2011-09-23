package org.osmsurround.ra.export;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.osmsurround.ra.context.AnalyzerContext;
import org.osmsurround.ra.data.Node;
import org.osmsurround.ra.graph.Graph;
import org.osmsurround.ra.graph.IntersectionNode;
import org.osmsurround.ra.traverse.TraverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportService {

	@Autowired
	private TraverseService traverseService;
	@Autowired
	private GpxExport gpxExport;

	public void export(AnalyzerContext analyzerContext, OutputStream out) {

		List<Section> containers = new ArrayList<Section>();

		List<Graph> graphs = analyzerContext.getGraphs();

		for (int graphIndex = 0; graphIndex < graphs.size(); graphIndex++) {

			Graph graph = graphs.get(graphIndex);

			IntersectionNode[] intersectionNodes = graph.getLeaves().toArray(new IntersectionNode[0]);
			if (intersectionNodes.length == 2 || intersectionNodes.length == 1) {

				IntersectionNode startNode = intersectionNodes[0];
				IntersectionNode endNode = intersectionNodes.length == 1 ? startNode : intersectionNodes[1];

				List<Node> list = traverseService.traverse(startNode, endNode);
				List<Node> completeNodeList = traverseService.fillInNodes(list, analyzerContext);
				SectionContainer sectionContainer = new SectionContainer("Graph " + (graphIndex + 1));
				sectionContainer.addCoordinates(completeNodeList);
				containers.add(sectionContainer);
			}
		}

		gpxExport.export(containers, out);

	}

	public void exportForDisplay(AnalyzerContext analyzerContext, OutputStream out) {
		SimpleSegmentConverter converter = new SimpleSegmentConverter();
		List<Section> containers = converter.convert(analyzerContext);
		gpxExport.export(containers, out);
	}
}