package com.ailk.service;

import com.ailk.model.ResultDTO;
import com.ailk.model.ValueSet;

public interface IQueryService {

	public ResultDTO loadData(ValueSet vs);
}
