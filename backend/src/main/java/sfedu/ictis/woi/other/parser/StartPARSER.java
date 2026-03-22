package sfedu.ictis.woi.other.parser;

import java.sql.Connection;
import java.sql.DriverManager;

public class StartPARSER {
    public static void main() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/walk", "walk", "walk");
        OsmPbfParser parser = new OsmPbfParser(conn);

        parser.parse("D:\\Desktop\\Student\\Diplom\\WalkOfInterest\\data\\taganrog_38.627_47.133_e784cfd6.osm.pbf");

        if (parser.getStatistics().get("processed_objects") % 50_000 == 0) {
            parser.clearCaches();
        }

        parser.close();

    }
}
