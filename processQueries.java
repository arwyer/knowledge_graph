import java.lang.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.*;
import com.google.common.base.Preconditions;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.SchemaStatus;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.core.schema.Mapping;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import java.util.HashMap;
import java.util.Map;
import de.ipk_gatersleben.util.CorpusContainer;
import org.janusgraph.core.attribute.Text;

/*
This program is going to combine the query suggestion code
with

*/
public class processQueries{


  public static void main(String args[]){
    String modelName = "/Users/arw/query/bioescorte-suggestion/data/output/model.bin";
    CorpusContainer corp = new CorpusContainer();

    String queryResultFileName = "queryResults.txt";

    try{
      FileWriter queryOut = new FileWriter(queryResultFileName);
      corp.loadModel(modelName);
      String graphFile = "/Users/arw/janusgraph-0.2.0-hadoop2/conf/janusgraph-cassandra-es.properties";
      JanusGraph graph = JanusGraphFactory.open(graphFile);
      GraphTraversalSource g = graph.traversal();

    //List<String> querySuggestions = corp.getSuggestsemantic("lignin","null");


      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String line = br.readLine();
      while(line != null){
        //List<Vertex> results = g.V().has("description",Text.textContains(line)).toList();
        List<Vertex> results = g.V().has("description",Text.textContains(line)).in().toList();
        //System.out.print(line);
        String resultString = "Query: " + line + "\n";
        List<String> querySuggestions = corp.getSuggestsemantic(line,"null");
        resultString += "Suggestions: ";
        for(String s : querySuggestions){
          resultString += s + " ";
        }
        resultString += "\n";
        resultString += "Edges:\n";
        for(Vertex Vert : results){
          Property<String> P = Vert.property("id");
          String geneID = P.value();
          //List<Vertex> pheno = g.V(Vert).outE("has_phenotype").inV().values("description")
          List<Vertex> phenos = g.V(Vert).outE("has_phenotype").inV().has("description",Text.textContains(line)).toList();
          for(Vertex Pheno : phenos){
            Property<String> Prop = Pheno.property("description");
            String desc = Prop.value();
            resultString += geneID + " has_phenotype " + desc.replace("_"," ") + "\n";

          }

          //resultString += geneID + " ";
          //queryOut.write(geneID + "\n");
        }
        resultString += "\n";
        queryOut.write(resultString);
        queryOut.flush();
        line = br.readLine();
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
    System.exit(0);
  }
}
