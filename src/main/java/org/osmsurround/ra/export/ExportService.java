package org.osmsurround.ra.export;

import org.osmtools.api.Section;
import org.osmtools.ra.context.AnalyzerContext;
import org.osmtools.ra.data.Node;
import org.osmtools.ra.export.SectionContainer;
import org.osmtools.ra.graph.Graph;
import org.osmtools.ra.graph.IntersectionNode;
import org.osmtools.ra.traverse.TraverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {

	@Autowired
	private TraverseService traverseService;
	@Autowired
	private GpxExport gpxExport;
	@Autowired
	private GeoJsonExport geoJsonExport;

	public void export(AnalyzerContext analyzerContext, OutputStream out, String format) {
		List<Section> containers = convertToSections(analyzerContext);
		if ("gpx".equals(format))
			gpxExport.export(containers, out);
		else if ("json".equals(format))
			geoJsonExport.export(containers, analyzerContext.getRelation(), out);
	}

	public List<Section> convertToSections(AnalyzerContext analyzerContext) {
		List<Section> containers = new ArrayList<Section>();
		List<Graph> graphs = analyzerContext.getGraphs();

		for (int graphIndex = 0; graphIndex < graphs.size(); graphIndex++) {

			Graph graph = graphs.get(graphIndex);

			IntersectionNode[] intersectionNodes = graph.getLeaves().toArray(new IntersectionNode[0]);
			if (intersectionNodes.length == 2 || intersectionNodes.length == 1) {

				IntersectionNode startNode = intersectionNodes[0];
				IntersectionNode endNode = intersectionNodes.length == 1 ? startNode : intersectionNodes[1];

				List<Node> list = traverseService.traverse(startNode, endNode);
				List<Node> completeNodeList = traverseService.fillInNodes(list, analyzerContext.getSegments());
				SectionContainer sectionContainer = new SectionContainer("Graph " + (graphIndex + 1));
				sectionContainer.addCoordinates(completeNodeList);
				containers.add(sectionContainer);
			}
		}
		return containers;
	}

	public void exportForDisplay(AnalyzerContext analyzerContext, OutputStream out) {
		SimpleSegmentConverter converter = new SimpleSegmentConverter();
		List<Section> containers = converter.convert(analyzerContext);
		gpxExport.export(containers, out);
	}
}
