package be.cronos.leos.benchmark.controller;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import be.cronos.leos.benchmark.dto.Data;
import be.cronos.leos.benchmark.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncManager;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Asynchronous data controller
 * @author Mihai VLASCEANU <vlascmi@cronos.be>
 */
@RestController
@Api(value="")
@RequiredArgsConstructor
public class AsyncRestController {

	private final DataService dataService;
	private final TaskExecutor executor;
	
	private Scheduler scheduler;
	
	@PostConstruct
	protected void initializeScheduler(){
		scheduler = Schedulers.from(executor);
	}
	
	/**
	 * Callable uses the task executor of {@link WebAsyncManager}
	 * @return
	 */
	@GetMapping(value="/callable/data", produces="application/json")
	@ApiOperation(value = "Gets data", notes="Gets data asynchronously")
	@ApiResponses(value={@ApiResponse(code=200, message="OK")})
	public Callable<List<Data>> getDataCallable(){
		return (dataService::loadData);
	}
	
	/**
	 * With DeferredResult you have to provide your own executor, the task is assumed to be asynchronous
	 * @return
	 */
	@GetMapping(value="/deferred/data", produces="application/json")
	@ApiOperation(value = "Gets data", notes="Gets data asynchronously")
	@ApiResponses(value={@ApiResponse(code=200, message="OK")})
	public DeferredResult<List<Data>> getDataDeferredResult(){
		DeferredResult<List<Data>> dr = new DeferredResult<>();
		Thread th = new Thread(() -> {
			List<Data> data = dataService.loadData();
			dr.setResult(data);
		},"MyThread");
		th.start();
		return dr;
	}
	
	@GetMapping(value="/observable-deferred/data", produces="application/json")
	@ApiOperation(value = "Gets data through Observable", notes="Gets data asynchronously through Observable")
	@ApiResponses(value={@ApiResponse(code=200, message="OK")})
	public DeferredResult<List<Data>> getDataObservable(){
		DeferredResult<List<Data>> dr = new DeferredResult<>();
		Observable<List<Data>> dataObservable = dataService.loadDataObservable();
		//XXX subscribeOn is necessary, otherwise it would be done in the http thread
		dataObservable.subscribeOn(scheduler).subscribe( 
				dr::setResult, 
				dr::setErrorResult);
		return dr;
	}
	
	@GetMapping(value="/observable/data", produces="application/json")
	@ApiOperation(value = "Gets data through Observable returning Observable", notes="Gets data asynchronously through Observable returning Observable")
	@ApiResponses(value={@ApiResponse(code=200, message="OK")})
	public Single<List<Data>> getDataObservable2(){
		Observable<List<Data>> dataObservable = dataService.loadDataObservable();
		//XXX subscribeOn is necessary, otherwise it would be done in the http thread
		return dataObservable.toSingle().subscribeOn(scheduler);
	}
	
	@GetMapping(value="/hystrix/data", produces="application/json")
	@ApiOperation(value = "Gets data hystrix", notes="Gets data asynchronously with hystrix")
	@ApiResponses(value={@ApiResponse(code=200, message="OK")})
	public Single<List<Data>> getDataHystrix(){
		Observable<List<Data>> observable = dataService.loadDataHystrix();
		//XXX subscribeOn is necessary, otherwise it would be done in the http thread
		return observable.toSingle().subscribeOn(scheduler);
	}
	
	@GetMapping(value="/hystrix-callable/data", produces="application/json")
	@ApiOperation(value = "Gets data hystrix", notes="Gets data asynchronously with hystrix")
	@ApiResponses(value={@ApiResponse(code=200, message="OK")})
	public Callable<List<Data>> getDataHystrixAsync() {
		Future<List<Data>> future = dataService.loadDataHystrixAsync();
		return future::get;
	}
	
}
