package com.ailk.service;

import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;

import java.io.IOException;

public interface IQueryService {

	public ResultDTO loadData(ValueSet vs);
}
