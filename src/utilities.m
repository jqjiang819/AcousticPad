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
        
        function out = isminmax(data, i)
        %ISMINMAX
        %  min: out = -1
        %  max: out = 1
        %  oth: out = 0
            num = 5;
            % get left value
            if i < num+1 && i > 1
                ism.left = mean(data(1:i-1));
            elseif i ~= 1
                ism.left = mean(data(i-num:i-1));
            end
            % get right value
            if i > length(data)-num && i < length(data)
                ism.right = mean(data(i+1:end));
            elseif i ~= length(data)
                ism.right = mean(data(i+1:i+num));
            end
            % check center value
            if i == 1 || i == length(data)
                ism.out = 0;
            elseif (data(i) > ism.left) && (data(i) > ism.right)
                ism.out = 1;
            elseif (data(i) < ism.left) && (data(i) < ism.right)
                ism.out = -1;
            else
                ism.out = 0;
            end
            out = ism.out;
        end

        function out = getextmean(ext)
        %GETEXTMEAN
            if strcmp(ext(1).type,'init') || strcmp(ext(2).type,'init')
                ise.out = 0;
            else
                ise.out = (ext(1).data+ext(2).data)/2;
            end
            out = ise.out;
        end

        function out = getstaticvec(data,s,i,ext)
        %GETSTATICVEC
            extmean = utilities.getextmean(ext);
            if extmean ~= 0
                vec.out = 0.9*s(i) + 0.1*extmean;
            else
                vec.out = 0.9*data(i);
            end
            out = vec.out;
        end
    end
    
end

