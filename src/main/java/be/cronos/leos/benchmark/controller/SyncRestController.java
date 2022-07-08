package be.cronos.leos.benchmark.controller;

import java.util.List;

import be.cronos.leos.benchmark.dto.Data;
import be.cronos.leos.benchmark.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Synchronous data controller
 * @author Mihai VLASCEANU <vlascmi@cronos.be>
 */
@RestController("SyncRestController")
@Api(value="")
@AllArgsConstructor
public class SyncRestController {

	private final DataService dataService;
	
	/**
	 * Returns {@link List< Data >}
	 */
	@GetMapping(value="/sync/data", produces="application/json")
	@ApiOperation(value = "Gets data", notes="Gets data synchronously")
	@ApiResponses(value={@ApiResponse(code=200, message="OK")})
	public List<Data> getData(){
		return dataService.loadData();
	}
	
}
