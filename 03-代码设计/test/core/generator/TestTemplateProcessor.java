package core.generator;
import core.common.*;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataSourceConfig.class)
@PowerMockIgnore("javax.management.*")
public class TestTemplateProcessor implements DataSourceType{
	//??????(SUT)??????????
	private TemplateProcessor tp;
	//?????(DOC)??????????
	private DataSourceConfig dsc;

	@Test
	public void testStaticVarExtract() throws Exception {

		//??????????????????????????
		tp.staticVarExtract("resource/newtemplatezzz.doc");
		//??????????????
		DataSource ds = dsc.getConstDataSource();

		List<DataHolder> dhs = ds.getVars();
		DataHolder dh1 = ds.getDataHolder("sex");
		assertNotNull("???sex??????", dh1);
		assertEquals("???sex????????","Female",dh1.getValue());

		DataHolder dh2 = ds.getDataHolder("readme");
		assertNotNull("???readme??????", dh2);
		assertEquals("???readme????????","5",dh2.getValue());

		DataHolder dh3 = ds.getDataHolder("testexpr");
		assertNotNull("???testexpr", dh3);
		assertEquals("???testexpr????????????","${num}+${readme}",dh3.getExpr());
		dh3.fillValue();
		assertEquals("???testexpr","5.0",dh3.getValue());

		//????UT????????????????????
		PowerMock.verifyAll();
	}

	@Before
	public void setUp() throws Exception {

		//??????Mock??????????????????????
		//????????????????????UT??emplateProcessor?????OC??ataSourceConfig???????????????????????
		//?????????????????????
		//????????
		//1. ???EasyMock???????ataSourceConfig???????ock????????
		//2. ?????????STUB?????????????????????????????
		//3. ???PowerMock???DataSourceConfig???????ock??
		//4. ????????ock?????????????????????????
        //------------------------------------------------
        //?????????????????
        //
        //
        // ????????
        //
        //------------------------------------------------
		//5. ?????????????
		DataHolder dhsex = EasyMock.createMock(DataHolder.class);
		dhsex.setName("sex");
		EasyMock.expect(dhsex.getValue()).andStubReturn("Female");
		DataHolder dhrm = EasyMock.createMock(DataHolder.class);
		dhrm.setName("readme");
		EasyMock.expect(dhrm.getValue()).andStubReturn("5");
		DataHolder dhte = EasyMock.createMock(DataHolder.class);
		dhte.setName("testexpr");
		EasyMock.expect(dhte.getExpr()).andStubReturn("${num}+${readme}");
		EasyMock.expect(dhte.fillValue()).andStubReturn(null);
		EasyMock.expect(dhte.getValue()).andStubReturn("5.0");
		//vars??onstDataSource??ataSource?????
		ArrayList<DataHolder> vars=new ArrayList<>();
		//DataHolder???ArrayList<DataHolder>
		vars.add(dhsex);
		vars.add(dhrm);
		vars.add(dhte);

		//cds将存入dataSources
		ConstDataSource cds=EasyMock.createMock(ConstDataSource.class);
		cds.setVars(vars);
		//DataSource中非静态方法
		EasyMock.expect(cds.getType()).andStubReturn("");
		EasyMock.expect(cds.getName()).andStubReturn("");
		EasyMock.expect(cds.getVars()).andStubReturn(vars);
		EasyMock.expect(cds.getDataHolder("sex")).andStubReturn(dhsex);
		EasyMock.expect(cds.getDataHolder("readme")).andStubReturn(dhrm);
		EasyMock.expect(cds.getDataHolder("testexpr")).andStubReturn(dhte);
		//dataSources为DataSourceConfig成员
		ArrayList<DataSource> dataSources = new ArrayList<>();
		dataSources.add(cds);
		dsc=EasyMock.createMock(DataSourceConfig.class);
		//DataSourceConfig中非静态方法
		EasyMock.expect(dsc.getDataSources()).andStubReturn(dataSources);
		EasyMock.expect(dsc.getFilename()).andStubReturn("name1");
		EasyMock.expect(dsc.getConstDataSource()).andStubReturn(cds);
		EasyMock.expect(dsc.getDataSource(null)).andStubReturn(cds);
		EasyMock.expect(dsc.getDataSource(null)).andStubReturn(cds);

		
		//???????ConstDataSource?DataHolder
		EasyMock.replay(cds);
		EasyMock.replay(dhsex, dhrm, dhte);
		//Powermock??mock?????????
		PowerMock.mockStatic(DataSourceConfig.class);
		EasyMock.expect(DataSourceConfig.newInstance()).andStubReturn(dsc);
		PowerMock.replayAll(dsc);
		//???????????????SUT??????
		tp = new TemplateProcessor();
	}
}
