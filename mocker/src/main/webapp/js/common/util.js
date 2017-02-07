function Trim(str, is_global) {
		var result;
		result = str.replace(/(^\s+)|(\s+$)/g, "");
		if (is_global.toLowerCase() == "g")
			result = result.replace(/\s/g, "");
		return result;
}