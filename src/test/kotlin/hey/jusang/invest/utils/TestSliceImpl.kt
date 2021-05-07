package hey.jusang.invest.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.domain.SliceImpl

class TestSliceImpl<T> @JsonCreator constructor(
    @JsonProperty("content") content: List<T>,
    @JsonProperty("pageable") pageable: JsonNode,
    @JsonProperty("size") size: Int,
    @JsonProperty("number") number: Int,
    @JsonProperty("sort") sort: JsonNode,
    @JsonProperty("numberOfElements") numberOfElements: Int,
    @JsonProperty("first") first: Boolean,
    @JsonProperty("last") last: Boolean,
    @JsonProperty("empty") empty: Boolean
) : SliceImpl<T>(content)