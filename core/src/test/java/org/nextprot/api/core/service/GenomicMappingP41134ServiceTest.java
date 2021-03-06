package org.nextprot.api.core.service;

import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Class used for testing Genomic Mapping DAO
 * 
 * @author dteixeira
 */
@DatabaseSetup(value = "GenomicMappingP41134Test.xml", type = DatabaseOperation.INSERT)
public class GenomicMappingP41134ServiceTest extends CoreUnitBaseTest {

	@Autowired
	GenomicMappingService genomicMappingService;

	@Test
	public void shouldGetTheGenomicMappingForP41134FromService() throws Exception {
		genomicMappingService.findGenomicMappingsByEntryName("NX_P41134");
	}
}
