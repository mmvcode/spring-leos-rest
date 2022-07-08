package be.cronos.leos.benchmark.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Data implements Serializable{

	private static final long serialVersionUID = 1049438747605741485L;
	@ApiModelProperty(required=true)
	private String key;
	@ApiModelProperty(required=true)
	private String value;
}
