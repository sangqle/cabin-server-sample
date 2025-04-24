package com.cabin.demo.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.lang.GeoLocation;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExifUtil {
    // EXIF tag constants
    private static final int TAG_MODEL = ExifDirectoryBase.TAG_MODEL;                // 0x0110, 272 :contentReference[oaicite:1]{index=1}
    private static final int TAG_LENS_MODEL = ExifDirectoryBase.TAG_LENS_MODEL;           // 0xA434, 42036 :contentReference[oaicite:2]{index=2}
    private static final int TAG_ISO = ExifSubIFDDirectory.TAG_ISO_EQUIVALENT;    // 0x8827, 34855 :contentReference[oaicite:3]{index=3}
    private static final int TAG_EXPOSURE_TIME = ExifSubIFDDirectory.TAG_EXPOSURE_TIME;      // 0x829A, 33434 :contentReference[oaicite:4]{index=4}
    private static final int TAG_FNUMBER = ExifSubIFDDirectory.TAG_FNUMBER;            // 0x829D, 33437 :contentReference[oaicite:5]{index=5}
    private static final int TAG_FOCAL_LENGTH = ExifSubIFDDirectory.TAG_FOCAL_LENGTH;       // 0x920A, 37386 :contentReference[oaicite:6]{index=6}
    private static final int TAG_FLASH = ExifSubIFDDirectory.TAG_FLASH;              // 0x9209, 37385 :contentReference[oaicite:7]{index=7}
    private static final int TAG_DATETIME_ORIGINAL = ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL;  // 0x9003, 36867 :contentReference[oaicite:8]{index=8}
    private static final int TAG_GPS_INFO_OFFSET = ExifDirectoryBase.TAG_SUBJECT_LOCATION;      // pointer to GPS IFD :contentReference[oaicite:9]{index=9}

    public static ExifData getExifData(byte[] fileContent) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(
                new ByteArrayInputStream(fileContent)
        );
        ExifData result = new ExifData();

        // Storage for EXIF data as string
        Iterable<Directory> directories = metadata.getDirectories();

        Map<String, Object> map = new HashMap<>();
        for (Directory dir : metadata.getDirectories()) {
            for (Tag tag : dir.getTags()) {
                map.put(dir.getName() + "." + tag.getTagName(), tag.getDescription());
            }
        }
        result.setExifMap(map);
        // 1) Iterate all tags once, dispatch by tagType
        for (Directory dir : metadata.getDirectories()) {
            for (Tag tag : dir.getTags()) {
                int type = tag.getTagType();
                String desc = tag.getDescription();

                switch (type) {
                    case TAG_MODEL:
                        result.setCameraModel(desc);
                        break;
                    case TAG_LENS_MODEL:
                        result.setLensModel(desc);
                        break;
                    case TAG_ISO:
                        result.setIso(Integer.valueOf(desc));
                        break;
                    case TAG_EXPOSURE_TIME:
                        result.setExposureTime(desc);
                        break;
                    case TAG_FNUMBER:
                        // desc like "f/1.9" → strip "f/" then parse
                        result.setFNumber(new BigDecimal(
                                desc.replaceAll("[^0-9\\.]", "")
                        ));
                        break;
                    case TAG_FOCAL_LENGTH:
                        // desc like "35.0 mm"
                        result.setFocalLength(new BigDecimal(
                                desc.replaceAll("[^0-9\\.]", "")
                        ));
                        break;
                    case TAG_FLASH:
                        result.setFlash(desc);
                        break;
                    case TAG_DATETIME_ORIGINAL:
                        Date d = dir.getDate(TAG_DATETIME_ORIGINAL);
                        if (d != null) {
                            result.setShootingTime(
                                    LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(d.getTime()),
                                            ZoneId.systemDefault()
                                    )
                            );
                        }
                        break;
                    default:
                        // no-op
                }
            }
        }

        // 2) GPS—handled separately via GpsDirectory for accuracy :contentReference[oaicite:10]{index=10}
        GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        if (gpsDir != null) {
            GeoLocation geo = gpsDir.getGeoLocation();
            if (geo != null && !geo.isZero()) {
                result.setLatitude(geo.getLatitude());
                result.setLongitude(geo.getLongitude());
            }
        }
        return result;
    }
}
