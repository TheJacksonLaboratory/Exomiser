/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2017 Queen Mary University of London.
 * Copyright (c) 2012-2016 Charité Universitätsmedizin Berlin and Genome Research Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monarchinitiative.exomiser.core.filters;

import org.monarchinitiative.exomiser.core.model.VariantEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class SparseVariantFilterRunner extends SimpleVariantFilterRunner {

    private static final Logger logger = LoggerFactory.getLogger(SparseVariantFilterRunner.class);

    /**
     * @param filters
     * @param variantEvaluations
     * @return
     */
    @Override
    public List<VariantEvaluation> run(List<VariantFilter> filters, List<VariantEvaluation> variantEvaluations) {
        logger.info("Filtering {} variants using sparse filtering...", variantEvaluations.size());

        if (ifThereAreNoFiltersToRun(filters)) {
            return variantEvaluations;
        }

        List<VariantEvaluation> filteredVariantEvaluations = runFilters(filters, variantEvaluations);

        int numRemoved = variantEvaluations.size() - filteredVariantEvaluations.size();
        logger.info("Filtering for {} removed {} of {} variants - returning {} filtered variants.", getFilterTypes(filters), numRemoved, variantEvaluations.size(), filteredVariantEvaluations.size());
        return filteredVariantEvaluations;
    }

    @Override
    public List<VariantEvaluation> run(VariantFilter filter, List<VariantEvaluation> variantEvaluations) {
        for (VariantEvaluation variantEvaluation : variantEvaluations) {
            if (variantEvaluation.passedFilters()) {
                run(filter, variantEvaluation);
            }
        }
        return makeListofFilteredVariants(variantEvaluations);
    }

    private boolean ifThereAreNoFiltersToRun(List<VariantFilter> filters) {
        if (filters.isEmpty()) {
            logger.info("Unable to filter variants against empty Filter list - returning all variants");
            return true;
        }
        return false;
    }

    private List<VariantEvaluation> runFilters(List<VariantFilter> filters, List<VariantEvaluation> variantEvaluations) {

        for (Filter filter : filters) {
            for (VariantEvaluation variantEvaluation : variantEvaluations) {
                //the only difference between sparse and full filtering is this if clause here...
                if (variantEvaluation.passedFilters()) {
                    run(filter, variantEvaluation);
                }
            }
        }
        return makeListofFilteredVariants(variantEvaluations);
    }

    private List<VariantEvaluation> makeListofFilteredVariants(List<VariantEvaluation> variantEvaluations) {
        List<VariantEvaluation> filteredVariantEvaluations = new ArrayList<>();
        for (VariantEvaluation variant : variantEvaluations) {
            if (variant.passedFilters()) {
                filteredVariantEvaluations.add(variant);
            }
        }
        return filteredVariantEvaluations;
    }

}
