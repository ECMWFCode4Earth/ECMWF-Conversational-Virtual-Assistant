package com._2horizon.cva.retrieval.confluence

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Frank Lieber (liefra) on 2020-06-12.
 */
class ConfluenceLinkExtractorTest extends Specification {

    @Shared
    ConfluenceLinkExtractor linkExtractor = new ConfluenceLinkExtractor()

    @Unroll
    def "Should extractInternalConfluenceLinks of #nodeId"() {
        given:
        def document = StorageFormatUtil.createDocumentFromStructuredStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        when:
        def links = linkExtractor.extractInternalConfluenceLinks(document, 'aSpaceKey')

        then:
        links.size() == s
        if (s > 0) {
            assert links.first().contentTitle == title
            assert links.first().anchor == anchor
        }

        where:
        nodeId    | s | title                                    | anchor
        181127817 | 5 | 'Climate Data Store (CDS) documentation' | null
        181122979 | 0 | ''                                       | null
        171412993 | 0 | ''                                       | null
    }

    @Unroll
    def "Should extractExternalLinks of #nodeId"() {
        given:
        def document = StorageFormatUtil.createDocumentFromStructuredStorageFormat(new File("./src/test/resources/data/confluence/storageformat/${nodeId}.html").text, true)

        when:
        def links = linkExtractor.extractExternalLinks(document)

        then:
        links.size() == s

        where:
        nodeId    | s
        181127817 | 10
        181122979 | 0
        171412993 | 0
    }

    @Unroll
    def "Should createEcmwfPublicationsLink of #href"() {
        when:
        ExternalConfluenceLink link = linkExtractor.createEcmwfPublicationsLink(href)

        then:
        link.type == ExternalConfluenceLinkType.ECMWF_PUBLICATION
        link.properties.get("id") == pubId

        where:
        href                                                          | pubId
        'https://www.ecmwf.int/en/elibrary/19305-part-i-observations' | '19305'

    }

    @Unroll
    def "Should createCdsLink of #href"() {
        when:
        ExternalConfluenceLink link = linkExtractor.createCdsLink(href)

        then:
        link.type == ExternalConfluenceLinkType.CDS_DATASET
        link.properties.get("id") == cdsId

        where:
        href                                                                                                                   | cdsId
        'https://cds.climate.copernicus.eu/cdsapp#!/dataset/reanalysis-era5-land-monthly-means?tab=overview'                   | 'reanalysis-era5-land-monthly-means'
        'https://cds.climate.copernicus.eu/cdsapp#!/software/app-globalshipping-arctic-route-availability-projections?tab=app' | 'app-globalshipping-arctic-route-availability-projections'

    }

    @Unroll
    def "Should createConfluenceLink of #href"() {
        when:
        ExternalConfluenceLink link = linkExtractor.createConfluenceLink(href)

        then:
        link.type == type
        link.properties.get("contentTitle") == contentTitle
        link.properties.get("spaceKey") == spaceKey
        link.properties.get("anchor") == anchor
        link.properties.get("pageId") == pageId

        where:
        href                                                                                                                                                              | pageId     | contentTitle                    | spaceKey | anchor                                                                                              | type
        'https://confluence.ecmwf.int/display/CKB/How+to+download+ERA5#HowtodownloadERA5-OptionB:DownloadERA5familydatathatisNOTlistedintheCDSonlinecatalogue-SLOWACCESS' | null       | 'How to download ERA5'          | 'CKB'    | 'HowtodownloadERA5-OptionB:DownloadERA5familydatathatisNOTlistedintheCDSonlinecatalogue-SLOWACCESS' | ExternalConfluenceLinkType.CONFLUENCE_LINK
        'https://confluence.ecmwf.int/display/CKB/ERA5-Land%3A+data+documentation'                                                                                        | null       | 'ERA5-Land: data documentation' | 'CKB'    | null                                                                                                | ExternalConfluenceLinkType.CONFLUENCE_LINK
        'https://confluence.ecmwf.int/pages/viewpage.action?pageId=74764925#ERA5:datadocumentation-Table4'                                                                | '74764925' | null                            | null     | null                                                                                                | ExternalConfluenceLinkType.CONFLUENCE_DIRECT_LINK
        'https://confluence.ecmwf.int/display/ECC'                                                                                                                        | null       | null                            | 'ECC'    | null                                                                                                | ExternalConfluenceLinkType.CONFLUENCE_SITE_LINK

    }
}
