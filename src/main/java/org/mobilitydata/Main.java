package org.mobilitydata;

import com.google.type.LatLng;
import org.mobilitydata.conversion.StopsConverter;
import org.mobilitydata.gtfsproto.StopsProto;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {



        long timeBeforeMillis = System.currentTimeMillis();

        // convert GTFS text files to .proto files on disk
        StopsConverter.convert();




        System.out.println("Took " + (System.currentTimeMillis() - timeBeforeMillis) + "ms");
    }
}
