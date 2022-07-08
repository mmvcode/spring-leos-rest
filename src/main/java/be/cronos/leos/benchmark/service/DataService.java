package be.cronos.leos.benchmark.service;

import java.util.List;
import java.util.concurrent.Future;

import be.cronos.leos.benchmark.dto.Data;

import rx.Observable;

public interface DataService {

    List<Data> loadData();

    Observable<List<Data>> loadDataHystrix();

    Future<List<Data>> loadDataHystrixAsync();

    Observable<List<Data>> loadDataObservable();

}