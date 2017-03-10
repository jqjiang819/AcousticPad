classdef utilities
   
    properties
    end
    
    methods(Static)
        function ext_out = initext()
            initext.ext(1).data = 0;
            initext.ext(1).type = 'init';
            initext.ext(2).data = 0;
            initext.ext(2).type = 'init';
            ext_out = initext.ext;
        end
    end
    
end

